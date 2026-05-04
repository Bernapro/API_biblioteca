package com.biblioteca.integration;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.biblioteca.controller.EjemplarController;
import com.biblioteca.dto.LibroEnriquecidoDTO;
import com.biblioteca.utilities.StringFormatUtil;

@Component
public class LoCClient {



	private final RestClient restClient;

	public LoCClient() {
		//URL base
		this.restClient = RestClient.builder().baseUrl("http://lx2.loc.gov:210/lcdb").build();
	}

	//este método ya no lo ocupo
	public LibroEnriquecidoDTO buscarClasificacionesPorTitulo(String tituloLimpio) {
		try {
			//petición GET.
			String xmlRespuesta = restClient.get()
					.uri(uriBuilder -> uriBuilder.queryParam("version", "2.0").queryParam("operation", "searchRetrieve")
							.queryParam("query", "dc.title=\"" + tituloLimpio + "\"").queryParam("recordSchema", "mods")
							.queryParam("maximumRecords", "1").build())
					.retrieve().body(String.class);
			System.out.println(xmlRespuesta);

			// null si no se encontró
			if (xmlRespuesta == null || xmlRespuesta.isBlank()) {
				System.out.println("No devolvió nada");
				return null;
			}


			return extraerClasificacionesMods(xmlRespuesta);

		} catch (RestClientException e) {
			System.err.println("Fallo la comunicación con LoC SRU: " + e.getMessage());
			return null;
		}
	}

	public LibroEnriquecidoDTO buscarHibrido(String isbn, String titulo, String autor) {

		// lógica: bath.isbn="isbn" OR (dc.title="titulo" AND
		// dc.creator="autor")
		StringBuilder cqlQuery = new StringBuilder("bath.isbn=\"").append(isbn).append("\"");

		if (titulo != null && !titulo.isBlank()) {
			cqlQuery.append(" or (dc.title=\"").append(titulo).append("\"");
			if (autor != null && !autor.isBlank()) {
				cqlQuery.append(" and dc.creator=\"").append(autor).append("\"");
			}
			cqlQuery.append(")");
		}

		try {
			String xmlRespuesta = restClient.get()
					.uri(uriBuilder -> uriBuilder.queryParam("version", "2.0").queryParam("operation", "searchRetrieve")
							.queryParam("query", cqlQuery.toString()).queryParam("recordSchema", "mods")
							.queryParam("maximumRecords", "1").build())
					.retrieve().body(String.class);
			System.out.println(xmlRespuesta);

			if (xmlRespuesta == null || xmlRespuesta.isBlank())
				return null;

			return extraerDatosMods(xmlRespuesta, isbn);

		} catch (RestClientException e) {
			System.err.println("Fallo la comunicación con LoC SRU: " + e.getMessage());
			return null;
		}
	}

	private LibroEnriquecidoDTO extraerClasificacionesMods(String xml) {
		LibroEnriquecidoDTO dto = new LibroEnriquecidoDTO();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xml)));

			//Se buscan las etiquertas estándar que incluye la respuesta, ej. la etiqueta "100" es para el autor
			NodeList nodosClasificacion = doc.getElementsByTagName("mods:classification");
			if (nodosClasificacion.getLength() == 0) {
				nodosClasificacion = doc.getElementsByTagName("classification");
			}

			for (int i = 0; i < nodosClasificacion.getLength(); i++) {
				Element elemento = (Element) nodosClasificacion.item(i);
				String autoridad = elemento.getAttribute("authority");
				String valor = elemento.getTextContent().trim();
				System.out.println(valor + '\n');

				if ("lcc".equalsIgnoreCase(autoridad) && dto.getLcc() == null) {
					dto.setLcc(valor);
				} else if ("ddc".equalsIgnoreCase(autoridad) && dto.getDewey() == null) {
					dto.setDewey(valor);
				} else if ("udc".equalsIgnoreCase(autoridad) && dto.getCdu() == null) {
					dto.setCdu(valor);
				}
			}
		} catch (Exception e) {
			System.err.println("Error parseando el XML MODS de la LoC: " + e.getMessage());
		}

		return dto;
	}

	private LibroEnriquecidoDTO extraerDatosMods(String xml, String isbnObjetivo) {
		LibroEnriquecidoDTO dto = new LibroEnriquecidoDTO();
		boolean isbnCoincide = false;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xml)));

			// Obteniendo los isbn
			NodeList identifiers = doc.getElementsByTagName("mods:identifier");
			if (identifiers.getLength() == 0)
				identifiers = doc.getElementsByTagName("identifier");

			for (int i = 0; i < identifiers.getLength(); i++) {
				Element el = (Element) identifiers.item(i);
				if ("isbn".equalsIgnoreCase(el.getAttribute("type"))) {
					//limpiar y comparar ambos ISBN
					String isbnLocLimpio = StringFormatUtil.limpiarIsbn(el.getTextContent().trim());
					System.out.println(isbnLocLimpio);
					System.out.println(isbnObjetivo);
					if (isbnLocLimpio.contains(isbnObjetivo) || isbnObjetivo.contains(isbnLocLimpio)) {
						isbnCoincide = true;
						break;
					}
				}
			}

			//la misma lógica que extraerClasificacionesMods(String xml), siempre, aunque no sea el mismo isbn
			NodeList nodosClasif = doc.getElementsByTagName("mods:classification");
			if (nodosClasif.getLength() == 0)
				nodosClasif = doc.getElementsByTagName("classification");

			for (int i = 0; i < nodosClasif.getLength(); i++) {
				Element el = (Element) nodosClasif.item(i);
				String autoridad = el.getAttribute("authority");
				String valor = el.getTextContent().trim();

				if ("lcc".equalsIgnoreCase(autoridad) && dto.getLcc() == null)
					dto.setLcc(valor);
				else if ("ddc".equalsIgnoreCase(autoridad) && dto.getDewey() == null)
					dto.setDewey(valor);
				else if ("udc".equalsIgnoreCase(autoridad) && dto.getCdu() == null)
					dto.setCdu(valor);
			}

			//solo si isbn coincide
			if (isbnCoincide) {
				System.out.println("coincidencia");
				NodeList originInfos = doc.getElementsByTagName("mods:originInfo");
				if (originInfos.getLength() == 0)
					originInfos = doc.getElementsByTagName("originInfo");

				if (originInfos.getLength() > 0) {
					Element originInfo = (Element) originInfos.item(0);
					NodeList editions = originInfo.getElementsByTagName("mods:edition");
					if (editions.getLength() == 0)
						editions = originInfo.getElementsByTagName("edition");
					if (editions.getLength() > 0) {
						dto.setEdicion(editions.item(0).getTextContent().trim());
						System.out.println(dto.getEdicion());
					}
					NodeList dates = originInfo.getElementsByTagName("mods:dateIssued");
					if (dates.getLength() == 0)
						dates = originInfo.getElementsByTagName("dateIssued");
					if (dates.getLength() > 0) {
						dto.setFechaPublicacion(
								StringFormatUtil.parsearFechaSegura(dates.item(0).getTextContent().trim()));
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error parseando MODS: " + e.getMessage());
		}

		return dto;
	}
}
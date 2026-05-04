package com.biblioteca.service;

import org.springframework.stereotype.Service;

import com.biblioteca.dto.LibroEnriquecidoDTO;
import com.biblioteca.integration.GoogleBooksClient;
import com.biblioteca.integration.LoCClient;
import com.biblioteca.integration.OpenLibraryClient;
import com.biblioteca.utilities.StringFormatUtil;

@Service
public class CatalogoOrquestadorService {

    private final GoogleBooksClient googleBooksClient;
    private final OpenLibraryClient openLibraryClient;
    private final LoCClient locClient;

    

    public CatalogoOrquestadorService(GoogleBooksClient googleBooksClient, OpenLibraryClient openLibraryClient,
			LoCClient locClient) {
		super();
		this.googleBooksClient = googleBooksClient;
		this.openLibraryClient = openLibraryClient;
		this.locClient = locClient;
	}



	public LibroEnriquecidoDTO buscarYEnriquecer(String isbnRaw) {
		String isbn = StringFormatUtil.limpiarIsbn(isbnRaw);
        LibroEnriquecidoDTO libroFinal = new LibroEnriquecidoDTO();
        libroFinal.setIsbn(isbn);

        //Llamada a GoogleBooks
        LibroEnriquecidoDTO datosGoogle = googleBooksClient.buscar(isbn);
        if (datosGoogle != null) {
            libroFinal.setTitulo(datosGoogle.getTitulo());
            libroFinal.setAutores(datosGoogle.getAutores());
            libroFinal.setEditorial(datosGoogle.getEditorial());
            libroFinal.setFechaPublicacion(datosGoogle.getFechaPublicacion());
            libroFinal.setCategorias(datosGoogle.getCategorias());
            libroFinal.setEdicion(datosGoogle.getEdicion());
        }

        // Llamada de respaldo en caso de que falten datos a Google
        if (libroFinal.getTitulo() == null || libroFinal.getEditorial() == null || libroFinal.getFechaPublicacion() == null || libroFinal.getEdicion() == null || libroFinal.getCategorias() == null) {
            LibroEnriquecidoDTO datosOL = openLibraryClient.buscar(isbn);
            
            if (datosOL != null) {
                if (libroFinal.getTitulo() == null) libroFinal.setTitulo(datosOL.getTitulo());
                if (libroFinal.getAutores() == null) libroFinal.setAutores(datosOL.getAutores());
                if (libroFinal.getEditorial() == null) libroFinal.setEditorial(datosOL.getEditorial());
                if (libroFinal.getFechaPublicacion() == null) libroFinal.setFechaPublicacion(datosOL.getFechaPublicacion());
                if (libroFinal.getCategorias() == null) libroFinal.setCategorias(datosOL.getCategorias());
                if (libroFinal.getEdicion() == null) libroFinal.setEdicion(datosOL.getEdicion());

            }
        }

        // Sí no hay título entonces no tiene sentido llamar a la LoC 
        if (libroFinal.getTitulo() == null) {
            return libroFinal;
        }

        //Obtener clasificación y otros datos más, la lógica de la llamada es isbn="isbn"OR(titulo="titulo"ANDautor="autor")
        //en caso de que el isbn esté registrado se van a llenar los campos que hacen falta (fecha y edición)
        //Si no solo se van a setear los códigos de clasificación (los que se retornen )
        String tituloLimpio = StringFormatUtil.limpiarParaLoC(libroFinal.getTitulo());
        String autorLimpio = "";
        if (libroFinal.getAutores() != null && !libroFinal.getAutores().isEmpty()) {
            autorLimpio = StringFormatUtil.limpiarParaLoC(libroFinal.getAutores().iterator().next());
        }
        LibroEnriquecidoDTO datosLoc = locClient.buscarHibrido(isbn, tituloLimpio, autorLimpio);

        if (datosLoc != null) {
  
            libroFinal.setLcc(datosLoc.getLcc());
            libroFinal.setDewey(datosLoc.getDewey());
            libroFinal.setCdu(datosLoc.getCdu());
            
            //se insertan los datos solo si LoC devolvió el mismo ISBN 
            if (datosLoc.getEdicion() != null && libroFinal.getEdicion() == null) {
                libroFinal.setEdicion(datosLoc.getEdicion());
            }
            if (datosLoc.getFechaPublicacion() != null && libroFinal.getFechaPublicacion() == null) {
                libroFinal.setFechaPublicacion(datosLoc.getFechaPublicacion());
            }
        }
        
        

        return libroFinal;
    }
}
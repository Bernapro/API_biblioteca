package com.biblioteca.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.biblioteca.dto.*;
import com.biblioteca.utilities.StringFormatUtil;

@Component
public class GoogleBooksClient {

    private final RestClient restClient;
    private final String apiKey;

    //Mi APIKEY desde el Application.properties
    public GoogleBooksClient(@Value("${google.books.api.key}") String apiKey) {
        this.apiKey = apiKey;
        // URL base
        this.restClient = RestClient.builder()
                .baseUrl("https://www.googleapis.com/books/v1/volumes")
                .build();
    }

    public LibroEnriquecidoDTO buscar(String isbn) {
        try {
            // petición GET
            GoogleBooksRespuesta respuesta = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("q", "isbn:" + isbn)
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .body(GoogleBooksRespuesta.class);

            // null si no se encuentra
            if (respuesta == null || respuesta.items() == null || respuesta.items().isEmpty()) {
                return null; 
            }

            //Información del volumeInfo
            VolumeInfo info = respuesta.items().get(0).volumeInfo();

            //Mapeo al DTO 
            LibroEnriquecidoDTO dto = new LibroEnriquecidoDTO();
            dto.setIsbn(isbn);
            dto.setTitulo(info.title());
            
            if (info.authors() != null) {
                dto.setAutores(info.authors());
            }
            if (info.publisher() != null) {
                dto.setEditorial(info.publisher());
            }
            
            if (info.categories() != null) {
                dto.setCategorias(info.categories());
            }
            if (info.publishedDate() != null) {
                dto.setFechaPublicacion(StringFormatUtil.parsearFechaSegura(info.publishedDate()));
            }

            return dto;

        } catch (RestClientException e) {
        	//en caso de no haber internet o los servidores estén caidos etcetc
            System.err.println("Fallo la comunicación con Google Books API: " + e.getMessage());
            return null;
        }
    }
}
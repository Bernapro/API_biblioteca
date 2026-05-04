package com.biblioteca.integration;


import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.biblioteca.dto.*;
import com.biblioteca.utilities.StringFormatUtil;


@Component
public class OpenLibraryClient {

    private final RestClient restClient;

    public OpenLibraryClient() {
        // URL base
        this.restClient = RestClient.builder()
                .baseUrl("https://openlibrary.org/api/books")
                .build();
    }

    public LibroEnriquecidoDTO buscar(String isbn) {
        String llaveDinamica = "ISBN:" + isbn;

        try {
        	//Open library retorna estructuras de "diccionario"
            ParameterizedTypeReference<Map<String, OpenLibraryBook>> tipoRespuesta = 
                new ParameterizedTypeReference<>() {};

            Map<String, OpenLibraryBook> respuesta = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("bibkeys", llaveDinamica)
                            .queryParam("format", "json")
                            .queryParam("jscmd", "data")
                            .build())
                    .retrieve()
                    .body(tipoRespuesta);

            //null si el libro no fue encontrado
            if (respuesta == null || !respuesta.containsKey(llaveDinamica)) {
                return null;
            }

            OpenLibraryBook info = respuesta.get(llaveDinamica);
            //extraer toda la info etcetcetc
            LibroEnriquecidoDTO dto = new LibroEnriquecidoDTO();
            dto.setIsbn(isbn);
            dto.setTitulo(info.title());

  
            if (info.authors() != null) {
                Set<String> nombresAutores = info.authors().stream()
                    .map(OpenLibraryEntity::name)
                    .collect(Collectors.toSet());
                dto.setAutores(nombresAutores);
            }

            if (info.publishers() != null && !info.publishers().isEmpty()) {
                // Tomamos solo la primera editorial si hay varias
                dto.setEditorial(info.publishers().get(0).name()); 
            }
            
            if (info.subjects() != null) {
                Set<String> categorias = info.subjects().stream()
                    .map(OpenLibraryEntity::name)
                    .collect(Collectors.toSet());
                dto.setCategorias(categorias);
            }
            if (info.publish_date() != null) {
                dto.setFechaPublicacion(StringFormatUtil.parsearFechaSegura(info.publish_date()));
            }

            return dto;

        } catch (RestClientException e) {
            System.err.println("Fallo la comunicación con OpenLibrary API: " + e.getMessage());
            return null;
        }
    }
}
package com.biblioteca.dto;

import java.util.UUID;

public record EjemplarLibroAutoresDTO(
		UUID id,
	    String noAdquisicion,
	    String titulo,
	    String[] autores) {

}

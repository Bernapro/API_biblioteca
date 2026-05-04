package com.biblioteca.dto;

import java.time.LocalDate;

public record LibroCompletoDTO(String isbn, String titulo, String editorial,
		String edicion, String[] autores, LocalDate fechaPublicacion, String[] categorias,
		String dewey, String clasificacionDelCongreso, String decimalUniversal, Long nEjemplares) {
}

package com.biblioteca.dto;

import java.util.Set;

public record LibroRespuestaDTO(String isbn, String titulo,
		Set<String> numsAdquisicion,String mensaje) {
}
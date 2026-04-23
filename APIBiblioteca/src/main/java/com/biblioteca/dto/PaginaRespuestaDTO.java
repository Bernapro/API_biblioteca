package com.biblioteca.dto;

import java.util.List;

public record PaginaRespuestaDTO<T>(List<T> contenido, int numeroPagina, // página actual
		int tamanoPagina, // elementos por página
		long totalElementos, // total de los registros
		int totalPaginas, // total de páginas
		boolean ultima // true si es la última pág.
) {
}
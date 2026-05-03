package com.biblioteca.dto;

public record LibroResumenDTO(
    String isbn,
    String titulo,
    String editorialNombre,
    long Ejemplares
) {}
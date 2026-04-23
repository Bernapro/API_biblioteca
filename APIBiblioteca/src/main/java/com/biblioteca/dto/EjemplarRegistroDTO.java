package com.biblioteca.dto;

import com.biblioteca.enums.CondicionEjemplar;

public record EjemplarRegistroDTO(
    String isbnLibro,
    String noAdquisicion,
    CondicionEjemplar condicion 
) {}
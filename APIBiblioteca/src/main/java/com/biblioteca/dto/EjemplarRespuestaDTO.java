package com.biblioteca.dto;

import java.util.UUID;

import com.biblioteca.enums.CondicionEjemplar;
import com.biblioteca.enums.EstadoEjemplar;

public record EjemplarRespuestaDTO(
    UUID idEjemplar,
    String noAdquisicion,
    String isbnLibro,
    EstadoEjemplar estado,
    CondicionEjemplar condicion,
    String mensaje
) {}
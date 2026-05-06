package com.biblioteca.dto;


public record PrestamosEstadoDTO(
    Long totales,
    Long vigentes,
    Long vencidos,
    Long proximosAVencer
) {}
package com.biblioteca.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PrestamoRespuestaDTO(UUID id, String usuario,
		LocalDate fechaLimite, int cantidad) {
}

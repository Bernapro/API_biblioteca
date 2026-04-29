package com.biblioteca.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PrestamoResumenDTO(UUID idPrestamo, String usuario,
		LocalDate fechaInicio, LocalDate fechaLimite, LocalDate fechaDevolucion) {

}

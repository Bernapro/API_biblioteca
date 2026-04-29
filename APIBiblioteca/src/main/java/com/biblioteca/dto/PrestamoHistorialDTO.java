package com.biblioteca.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PrestamoHistorialDTO(UUID idPrestamo, LocalDate fechaInicio, LocalDate fechaLimite,
		LocalDate fechaDevolucion, int cantidadLibros, String estado, long diasAtraso) {}
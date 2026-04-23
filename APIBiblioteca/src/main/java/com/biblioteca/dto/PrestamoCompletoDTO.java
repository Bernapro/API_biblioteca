package com.biblioteca.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PrestamoCompletoDTO(UUID idPrestamo, LocalDate fechaInicio, LocalDate fechaLimite,
		LocalDate fechaDevolucion, int cantidadLibros, String estado) {}

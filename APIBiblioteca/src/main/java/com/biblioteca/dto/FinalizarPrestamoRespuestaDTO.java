package com.biblioteca.dto;

import java.time.LocalDate;
import java.util.UUID;

public record FinalizarPrestamoRespuestaDTO(UUID prestamo, String usuario,
		LocalDate fecha, int cantidad, String mensaje) {}
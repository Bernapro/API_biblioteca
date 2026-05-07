package com.biblioteca.dto;

import java.time.LocalDate;

public record ExtenderPrestamoDTO(LocalDate fechaAntigua, LocalDate fechaNueva, String mensaje) {

}

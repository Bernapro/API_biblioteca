package com.biblioteca.dto;

import java.time.LocalDate;
import java.util.Set;

/*
 * Solo se piden las llaves primarias para que no sea demasiado pesado ingresar un libro
 * en la interfaz ya se verá como se insertan las editoriales, autores o categorías en
 * caso de no existir.
 * Esta clase define como construir un libro, se usarán los Id´s para mapearlo desde la 
 * clase de servicio correspondiente
 * */
public record LibroRegistroDTO(
		// sé que deberían ser atributos privados, pero necesito que la clase sea
		// inmutable
		// por eso uso record en lugar de class, y record no permite modificadores de
		// acceso
		// ya que por defecto los atributos son private final.
		// además genera los getters
		String isbn, String titulo, Long editorialId, String edicion, LocalDate fechaPublicacion, String dewey,
		String clasificacionDelCongreso, String clasificacionDecimalUniversal, Set<Long> autoresIds,
		Set<Long> categoriasIds) {
}
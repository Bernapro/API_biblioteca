package com.biblioteca.utilities;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormatUtil {

	public static String limpiarParaLoC(String texto) {
		if (texto == null || texto.isBlank()) {
			return "";
		}
		// Descomponer los caracteres (acentos, ñ, etc)
		// ejemplo 'á' = 'a''´'; 'ñ' = 'n''~'
		String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);

		// Eliminar todos los dialítricos con una RE
		return textoNormalizado.replaceAll("\\p{M}", "");
	}

	public static LocalDate parsearFechaSegura(String fechaRaw) {
		if (fechaRaw == null || fechaRaw.isBlank())
			return null;

		try {
			// primer caso ideal, si no se asume AÑO-01-01
			if (fechaRaw.matches("\\d{4}-\\d{2}-\\d{2}"))
				return LocalDate.parse(fechaRaw);
			if (fechaRaw.matches("\\d{4}-\\d{2}"))
				return LocalDate.parse(fechaRaw + "-01");
			if (fechaRaw.matches("\\d{4}"))
				return LocalDate.parse(fechaRaw + "-01-01");
			// por si se devuelven textos cómo: Octubre 15, 2003; sólo se toma el año
			Matcher matcher = Pattern.compile("\\b(1\\d{3}|2\\d{3})\\b").matcher(fechaRaw);
			if (matcher.find()) {
				return LocalDate.of(Integer.parseInt(matcher.group()), 1, 1);
			}
		} catch (Exception e) {

			System.err.println("No se pudo parsear la fecha: " + fechaRaw);
		}
		return null;
	}

	public static String limpiarIsbn(String isbn) {
		if (isbn == null)
			return "";
		// RE que elimina todo lo que no sea un número del 0 al 9 o la letra X (mayúscula o
		// minúscula)
		return isbn.replaceAll("[^0-9Xx]", "").toUpperCase();
	}
}
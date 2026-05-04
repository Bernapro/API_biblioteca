package com.biblioteca.dto;

import java.time.LocalDate;
import java.util.Set;

public class LibroEnriquecidoDTO {
    private String isbn;
    private String titulo;
    private Set<String> autores;
    private String edicion;
    private String editorial;
    private LocalDate fechaPublicacion;
    private Set<String> categorias;
    private String lcc;
    private String dewey;
    private String cdu;
    
    
	public LibroEnriquecidoDTO(String isbn, String titulo, Set<String> autores, String edicion, String editorial,
			LocalDate fechaPublicacion, Set<String> categorias, String lcc, String dewey, String cdu) {
		super();
		this.isbn = isbn;
		this.titulo = titulo;
		this.autores = autores;
		this.edicion = edicion;
		this.editorial = editorial;
		this.fechaPublicacion = fechaPublicacion;
		this.categorias = categorias;
		this.lcc = lcc;
		this.dewey = dewey;
		this.cdu = cdu;
	}
    
    public LibroEnriquecidoDTO() {
    	
    }

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Set<String> getAutores() {
		return autores;
	}

	public void setAutores(Set<String> autores) {
		this.autores = autores;
	}

	public String getEdicion() {
		return edicion;
	}

	public void setEdicion(String edicion) {
		this.edicion = edicion;
	}

	public String getEditorial() {
		return editorial;
	}

	public void setEditorial(String editorial) {
		this.editorial = editorial;
	}

	public LocalDate getFechaPublicacion() {
		return fechaPublicacion;
	}

	public void setFechaPublicacion(LocalDate fechaPublicacion) {
		this.fechaPublicacion = fechaPublicacion;
	}

	public Set<String> getCategorias() {
		return categorias;
	}

	public void setCategorias(Set<String> categorias) {
		this.categorias = categorias;
	}

	public String getLcc() {
		return lcc;
	}

	public void setLcc(String lcc) {
		this.lcc = lcc;
	}

	public String getDewey() {
		return dewey;
	}

	public void setDewey(String dewey) {
		this.dewey = dewey;
	}

	public String getCdu() {
		return cdu;
	}

	public void setCdu(String cdu) {
		this.cdu = cdu;
	}
    
    
}
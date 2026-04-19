package entity;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Libro {
	
	@Id
	private String isbn;
	
	@Column
	private String titulo;
	
	@ManyToMany
	@JoinTable(
			name = "libro_autor",
			joinColumns = @JoinColumn(name = "isbn"),
			inverseJoinColumns = @JoinColumn(name = "autor")
			)
	private Set<Autor> autores;
	
	@ManyToOne
	@JoinColumn(name = "editorial")
	private Editorial editorial;
	
	@Column(length = 30)
	private String edicion;
	
	@Column(name = "fecha_publicacion")
	private LocalDate fechaPublicacion;
	
	@Column
	private String dewey;
	
	@Column(name = "claseficacion_congreso")
	private String clasificacionDelCongreso;
	
	@Column(name = "clasificacion_dec_universal")
	private String clasificacionDecimalUniversal;
	
	@ManyToMany
	@JoinTable(
			name = "libro_categoria",
			joinColumns = @JoinColumn(name = "isbn"),
			inverseJoinColumns = @JoinColumn(name = "categoria")
			)
	private Set<Categoria> categorias;
	
	public Libro() {
		
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

	public Set<Autor> getAutores() {
		return autores;
	}

	public void setAutores(Set<Autor> autores) {
		this.autores = autores;
	}

	public Editorial getEditorial() {
		return editorial;
	}

	public void setEditorial(Editorial editorial) {
		this.editorial = editorial;
	}

	public String getEdicion() {
		return edicion;
	}

	public void setEdicion(String edicion) {
		this.edicion = edicion;
	}

	public LocalDate getFechaPublicacion() {
		return fechaPublicacion;
	}

	public void setFechaPublicacion(LocalDate fechaPublicacion) {
		this.fechaPublicacion = fechaPublicacion;
	}

	public String getDewey() {
		return dewey;
	}

	public void setDewey(String dewey) {
		this.dewey = dewey;
	}

	public String getClasificacionDelCongreso() {
		return clasificacionDelCongreso;
	}

	public void setClasificacionDelCongreso(String clasificacionDelCongreso) {
		this.clasificacionDelCongreso = clasificacionDelCongreso;
	}

	public String getClasificacionDecimalUniversal() {
		return clasificacionDecimalUniversal;
	}

	public void setClasificacionDecimalUniversal(String clasificacionDecimalUniversal) {
		this.clasificacionDecimalUniversal = clasificacionDecimalUniversal;
	}

	public Set<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(Set<Categoria> categorias) {
		this.categorias = categorias;
	}
	
	
}

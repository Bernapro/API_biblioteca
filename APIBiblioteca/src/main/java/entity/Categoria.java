package entity;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Categoria {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORIA_GEN")
	@SequenceGenerator(
        name = "CATEGORIA_GEN",               
        sequenceName = "CATEGORIA_SEQ",       //nombre rea en PostgreSQL
        allocationSize = 1                // id++
    )
	private Long id;
	
	@Column(length = 30, name = "categoria", nullable = false, unique = true)
	private String nombre;
	
	@ManyToMany(mappedBy = "categorias", fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<Libro> libros;
	
	public Categoria() {
		
	}
	
	public Set<Libro> getLibros() {
		return libros;
	}


	public void setLibros(Set<Libro> libros) {
		this.libros = libros;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nombre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Categoria other = (Categoria) obj;
		return Objects.equals(nombre, other.nombre);
	}

	
}

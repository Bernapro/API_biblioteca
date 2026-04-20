package com.biblioteca.entity;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Editorial {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EDITORIAL_GEN")
	@SequenceGenerator(
        name = "EDITORIAL_GEN",               
        sequenceName = "EDITORIAL_SEQ",       //nombre rea en PostgreSQL
        allocationSize = 1                // id++
    )
	private Long id;

	@Column(length = 30, name = "editorial", nullable = false, unique = true)
	private String nombre;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pais")
	private Pais pais;
	
	@OneToMany(mappedBy = "editorial", fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<Libro> libros;
	
	public Editorial() {
		
	}

	public Set<Libro> getLibros() {
		return libros;
	}


	public void setLibros(Set<Libro> libros) {
		this.libros = libros;
	}


	public Pais getPais() {
		return pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
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
		Editorial other = (Editorial) obj;
		return Objects.equals(nombre, other.nombre);
	}
	
	
	
	
}

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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Autor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AUTOR_GEN")
	@SequenceGenerator(
        name = "AUTOR_GEN",               
        sequenceName = "AUTOR_SEQ",       //nombre rea en PostgreSQL
        allocationSize = 1                // id++
    )
	private Long id;
	
	@Column(nullable = false, unique = true, length = 30)
	private String pseudonimo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pais")
	private Pais pais;
	
	@ManyToMany(mappedBy = "autores", fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<Libro> obras;
	
	public Autor() {	
	}
	
	
	public Set<Libro> getObras() {
		return obras;
	}


	public void setObras(Set<Libro> obras) {
		this.obras = obras;
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
	public String getPseudonimo() {
		return pseudonimo;
	}
	public void setPseudonimo(String pseudonimo) {
		this.pseudonimo = pseudonimo;
	}


	@Override
	public int hashCode() {
		return Objects.hash(pseudonimo);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Autor other = (Autor) obj;
		return Objects.equals(pseudonimo, other.pseudonimo);
	}
		
	
}

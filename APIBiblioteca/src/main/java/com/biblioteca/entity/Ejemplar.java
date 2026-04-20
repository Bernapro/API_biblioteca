package com.biblioteca.entity;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.biblioteca.enums.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Ejemplar {

	@Id
	private UUID id = UUID.randomUUID();
	
	@Column(nullable = false, unique = true, name = "no_adquisicion")
	private String noAdquisicion;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Libro libro;
	
	@Enumerated(EnumType.STRING) 
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false)
	private EstadoEjemplar estado;
	
	@Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "condicion", nullable = false)
    private CondicionEjemplar condicion;
	
	@OneToMany(mappedBy = "ejemplar", fetch =  FetchType.LAZY)
	@JsonIgnore
	private Set<DetallePrestamo> prestamos;
	
	public Ejemplar() {
		
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getNoAdquisicion() {
		return noAdquisicion;
	}

	public void setNoAdquisicion(String noAdquisicion) {
		this.noAdquisicion = noAdquisicion;
	}

	public Libro getLibro() {
		return libro;
	}

	public void setLibro(Libro libro) {
		this.libro = libro;
	}

	public EstadoEjemplar getEstado() {
		return estado;
	}

	public void setEstado(EstadoEjemplar estado) {
		this.estado = estado;
	}

	public CondicionEjemplar getCondicion() {
		return condicion;
	}

	public void setCondicion(CondicionEjemplar condicion) {
		this.condicion = condicion;
	}

	public Set<DetallePrestamo> getPrestamos() {
		return prestamos;
	}

	public void setPrestamos(Set<DetallePrestamo> prestamos) {
		this.prestamos = prestamos;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ejemplar other = (Ejemplar) obj;
		return Objects.equals(id, other.id);
	}
	
	
	
}

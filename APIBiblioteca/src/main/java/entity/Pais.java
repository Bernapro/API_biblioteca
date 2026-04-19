package entity;



import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Pais {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAIS_SEQ")
	private Long id;
	
	@Column(length = 30, name = "pais", nullable = false, unique = true)
	private String nombre;
	
	@OneToMany(mappedBy = "pais", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Editorial> editoriales;
	
	@OneToMany(mappedBy = "pais", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Autor> autores;
	
	public Pais() {
		
		
	}
	

	public List<Editorial> getEditoriales() {
		return editoriales;
	}


	public void setEditoriales(List<Editorial> editoriales) {
		this.editoriales = editoriales;
	}


	public List<Autor> getAutores() {
		return autores;
	}


	public void setAutores(List<Autor> autores) {
		this.autores = autores;
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
		Pais other = (Pais) obj;
		return Objects.equals(nombre, other.nombre);
	}
	
	
}

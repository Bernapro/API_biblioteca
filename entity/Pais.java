package entity;



import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Pais {

	@Id
	private Long id;
	
	@Column(length = 30, name = "pais")
	private String nombre;
	
	@OneToMany(mappedBy = "pais")
	private List<Editorial> editoriales;
	
	@OneToMany(mappedBy = "pais")
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
	
	
}

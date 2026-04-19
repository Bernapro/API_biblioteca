package entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Autor {
	
	@Id
	@GeneratedValue(strategy = GenerationType .SEQUENCE, generator = "ATOR_SEQ")
	private Long id;
	
	@Column(nullable = false, unique = true, length = 30)
	private String pseudonimo;
	
	@ManyToOne
	@JoinColumn(name = "pais")
	private Pais pais;
	
	@ManyToMany(mappedBy = "autores")
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
	
}

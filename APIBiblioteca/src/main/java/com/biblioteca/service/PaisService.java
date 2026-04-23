package com.biblioteca.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.entity.Pais;
import com.biblioteca.errorHandling.Exception.ResourceNotFoundException;
import com.biblioteca.repository.PaisRepository;

@Service
public class PaisService {

	private final PaisRepository paisrepository;

	public PaisService(PaisRepository paisRepository) {
		this.paisrepository = paisRepository;
	}

	@Transactional
	public Pais registrarPais(Pais pais) {
		// Agregaré validaciones
		return paisrepository.save(pais);
	}

	@Transactional(readOnly = true) // para la optimización de la consulta en BD
	public List<Pais> obtenerTodos() {
		return paisrepository.findAll();
	}

	@Transactional(readOnly = true)
	public Pais obtenerPorId(Long id) {
		return paisrepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("El país con " + id + " no existe."));
	}
	
}

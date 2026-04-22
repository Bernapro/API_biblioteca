package com.biblioteca.service;

import java.util.List;
import com.biblioteca.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.biblioteca.entity.Categoria;
import com.biblioteca.errorHandling.Exeption.ResourceNotFoundException;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

	public CategoriaService(CategoriaRepository categoriaRepository) {
		this.categoriaRepository = categoriaRepository;
	}

	@Transactional
	public Categoria registrarCategoria(Categoria categoria) {
		// Agregaré validaciones
		return categoriaRepository.save(categoria);
	}

	@Transactional(readOnly = true) // para la optimización de la consulta en BD
	public List<Categoria> obtenerTodos() {
		return categoriaRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Categoria obtenerPorId(Long id) {
		return categoriaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("La categoría con " + id + " no existe."));
	}
	
	/*
	 * los se4rvicios de categoria, autor, editorial y país son mas o menos los mismos, la misma base,
	 * son entidades que se tratan de manera similar, forman parte de los otros objetos (libto por ejemplo)
	 * sin embargo no interactúan directamente con el usuario
	*/
}

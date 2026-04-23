package com.biblioteca.service;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.entity.Autor;
import com.biblioteca.errorHandling.Exception.*;
import com.biblioteca.repository.AutorRepository;

@Service
public class AutorService {

    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    @Transactional
    public Autor registrarAutor(Autor autor) {
        // Agregaré validaciones 
        return autorRepository.save(autor);
    }

    @Transactional(readOnly = true) // para la optimización de la consulta en BD
    public List<Autor> obtenerTodos() {
        return autorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Autor obtenerPorId(Long id) {
        return autorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El autor con ID " + id + " no existe."));
    }
}
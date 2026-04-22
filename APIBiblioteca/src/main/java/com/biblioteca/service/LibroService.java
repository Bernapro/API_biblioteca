package com.biblioteca.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.dto.LibroRegistroDTO;
import com.biblioteca.entity.Autor;
import com.biblioteca.entity.Categoria;
import com.biblioteca.entity.Editorial;
import com.biblioteca.entity.Libro;
import com.biblioteca.errorHandling.Exeption.*;
import com.biblioteca.repository.AutorRepository;
import com.biblioteca.repository.CategoriaRepository;
import com.biblioteca.repository.EditorialRepository;
import com.biblioteca.repository.LibroRepository;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final EditorialRepository editorialRepository;
    private final AutorRepository autorRepository;
    private final CategoriaRepository categoriaRepository;

    public LibroService(LibroRepository libroRepository, 
                        EditorialRepository editorialRepository,
                        AutorRepository autorRepository, 
                        CategoriaRepository categoriaRepository) {
        this.libroRepository = libroRepository;
        this.editorialRepository = editorialRepository;
        this.autorRepository = autorRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Libro registrarLibro(LibroRegistroDTO dto) {
        
    	//se mapean los campos propios del objeto Libro
        Libro libro = new Libro();
        libro.setIsbn(dto.isbn());
        libro.setTitulo(dto.titulo());
        libro.setEdicion(dto.edicion());
        libro.setFechaPublicacion(dto.fechaPublicacion());
        libro.setDewey(dto.dewey());
        libro.setClasificacionDelCongreso(dto.clasificacionDelCongreso());
        libro.setClasificacionDecimalUniversal(dto.clasificacionDecimalUniversal());

        //luego se validan los id´s de los objtos que se relacionan con el libro
        //evidentemente, si persisten en la BD se setea el atributo del obj libro
        if (dto.editorialId() != null) {
            Editorial editorial = editorialRepository.findById(dto.editorialId())
                .orElseThrow(() -> new ResourceNotFoundException("La editorial con ID " + dto.editorialId() + " no está registrada."));
            libro.setEditorial(editorial);
        } else {
            throw new IllegalArgumentException("Todo libro debe tener una editorial asociada.");
        }

        //Es una lógica muy similar para la validación de autores, editoriales y categorias
        //más adelante veré como abstraerlo para evitar repetir código
        if (dto.autoresIds() != null && !dto.autoresIds().isEmpty()) {
            Set<Autor> autores = new HashSet<>();
            for (Long id : dto.autoresIds()) {
                Autor autor = autorRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("El autor con ID " + id + " no está registrado."));
                autores.add(autor);
            }
            libro.setAutores(autores);
        } else {
            throw new IllegalArgumentException("El libro debe tener al menos un autor.");
        }

        if (dto.categoriasIds() != null && !dto.categoriasIds().isEmpty()) {
            Set<Categoria> categorias = new HashSet<>();
            for (Long id : dto.categoriasIds()) {
                Categoria categoria = categoriaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("La categoría con ID " + id + " no está registrada."));
                categorias.add(categoria);
            }
            libro.setCategorias(categorias);
        }

        // si llegue hasta aquí supongo que todo salió bien
        return libroRepository.save(libro);
    }
}
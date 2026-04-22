package com.biblioteca.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.dto.LibroRegistroDTO;
import com.biblioteca.dto.LibroRespuestaDTO;
import com.biblioteca.entity.Libro;
import com.biblioteca.service.LibroService;

@RestController
@RequestMapping("/biblioteca/libros")
public class LibroController {

    private final LibroService libroService;
    
    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @PostMapping
    public ResponseEntity<LibroRespuestaDTO> registrarLibro(@RequestBody LibroRegistroDTO dto) {
        
        Libro libroGuardado = libroService.registrarLibro(dto);
        
        LibroRespuestaDTO respuesta = new LibroRespuestaDTO(
            libroGuardado.getIsbn(),
            libroGuardado.getTitulo(),
            "El libro ha sido registrado exitosamente en el catálogo."
        );
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
}
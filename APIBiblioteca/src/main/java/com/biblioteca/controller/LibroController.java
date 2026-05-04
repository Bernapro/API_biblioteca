package com.biblioteca.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.dto.LibroCompletoDTO;
import com.biblioteca.dto.LibroEnriquecidoDTO;
import com.biblioteca.dto.LibroRegistroDTO;
import com.biblioteca.dto.LibroRespuestaDTO;
import com.biblioteca.dto.LibroResumenDTO;
import com.biblioteca.dto.PaginaRespuestaDTO;
import com.biblioteca.entity.Libro;
import com.biblioteca.service.CatalogoOrquestadorService;
import com.biblioteca.service.LibroService;

@RestController
@RequestMapping("/biblioteca/libros")
public class LibroController {

    private final LibroService libroService;
    private final CatalogoOrquestadorService orquestadorService;
    public LibroController(LibroService libroService, CatalogoOrquestadorService orquestadorService) {
        this.libroService = libroService;
        this.orquestadorService = orquestadorService;
    }

    @PostMapping
    public ResponseEntity<LibroRespuestaDTO> registrarLibro(@RequestBody LibroRegistroDTO dto) {
        
        Libro libroGuardado = libroService.registrarLibro(dto);
        
        LibroRespuestaDTO respuesta = new LibroRespuestaDTO(
            libroGuardado.getIsbn(),
            libroGuardado.getTitulo(),
            libroGuardado.getNumAdquisicion(),
            "El libro ha sido registrado exitosamente en el catálogo."
        );
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<PaginaRespuestaDTO<LibroResumenDTO>> listarLibrosPaginados(
            @RequestParam(defaultValue = "0") int nPage,
            @RequestParam(defaultValue = "10") int len
    ) {
        PaginaRespuestaDTO<LibroResumenDTO> catalogo = libroService.obtenerCatalogo(nPage, len);
        return ResponseEntity.ok(catalogo);
    }
    
    @GetMapping("/{isbn}")
    public ResponseEntity<LibroCompletoDTO> obtenerLibroPorIsbn(@PathVariable String isbn) {
        LibroCompletoDTO libroCompleto = libroService.obtenerLibroCompleto(isbn);
        return ResponseEntity.ok(libroCompleto);
    }
    
    @GetMapping("resumen/{isbn}")
    public ResponseEntity<LibroResumenDTO> obtenerLibroResumidoPorIsbn(@PathVariable String isbn) {
        LibroResumenDTO libroResumido = libroService.obtenerLibroResumido(isbn);
        return ResponseEntity.ok(libroResumido);
    }
    
    @GetMapping("/autocompletar/{isbn}")
    public ResponseEntity<LibroEnriquecidoDTO> autocompletarLibro(@PathVariable("isbn") String isbn) {
        
        //Única llamada al servicio
        LibroEnriquecidoDTO libroEnriquecido = orquestadorService.buscarYEnriquecer(isbn);

        //Si el roquestador devolvió null entonces ninguna API logró encontrar el libro
        if (libroEnriquecido == null || libroEnriquecido.getTitulo() == null) {
            return ResponseEntity.notFound().build(); 
        }

        return ResponseEntity.ok(libroEnriquecido);
    }
}
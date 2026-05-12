package com.biblioteca.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import com.biblioteca.dto.LibroCompletoDTO;
import com.biblioteca.dto.LibroEnriquecidoDTO;
import com.biblioteca.dto.LibroRegistroDTO;
import com.biblioteca.dto.LibroRespuestaDTO;
import com.biblioteca.dto.LibroResumenDTO;
import com.biblioteca.dto.PaginaRespuestaDTO;
import com.biblioteca.entity.Libro;
import com.biblioteca.service.CatalogoOrquestadorService;
import com.biblioteca.service.LibroService;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/biblioteca/libros")
@Tag(name = "Catálogo de Libros", description = "Operaciones para gestionar el catálogo bibliográfico (concepto de libro)")
public class LibroController {

    private final LibroService libroService;
    private final CatalogoOrquestadorService orquestadorService;
    public LibroController(LibroService libroService, CatalogoOrquestadorService orquestadorService) {
        this.libroService = libroService;
        this.orquestadorService = orquestadorService;
    }
    
    @Operation(summary = "Registrar un nuevo Libro", 
            description = "Registra un nuevo libro y genera los números de Adquisición para los ejemplares")
    //Registrar un nuevo Libro
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
    
    @Operation(summary = "Listar los libros por página", 
            description = "Otorga una lista de libros (información resumida) y los entrega por página")
  //Obtener los libros de manera páginada, con opción de elegir la página y la longitud de esta
    @GetMapping
    public ResponseEntity<PaginaRespuestaDTO<LibroResumenDTO>> listarLibrosPaginados(
            @RequestParam(defaultValue = "0") int nPage,
            @RequestParam(defaultValue = "10") int len
    ) {
        PaginaRespuestaDTO<LibroResumenDTO> catalogo = libroService.obtenerCatalogo(nPage, len);
        return ResponseEntity.ok(catalogo);
    }
    
    @Operation(summary = "Obtener un libro", 
            description = "Buscar y obtener un libro mediante su isbn")
  //Consultar la información completa de un Libro
    @GetMapping("/{isbn}")
    public ResponseEntity<LibroCompletoDTO> obtenerLibroPorIsbn(@PathVariable String isbn) {
        LibroCompletoDTO libroCompleto = libroService.obtenerLibroCompleto(isbn);
        return ResponseEntity.ok(libroCompleto);
    }
    
  //Obtener un resumen de la información de un Libro
    @Operation(summary = "Obtener el resumen de un libro", 
            description = "Buscar y obtener el resumen de los datos de un libro por su isbn")
    @GetMapping("resumen/{isbn}")
    public ResponseEntity<LibroResumenDTO> obtenerLibroResumidoPorIsbn(@PathVariable String isbn) {
        LibroResumenDTO libroResumido = libroService.obtenerLibroResumido(isbn);
        return ResponseEntity.ok(libroResumido);
    }
    
  //Retorna la información completa (si existe) de un libro utilizando clientes como: "GoogleBooksAPI", "OpenLibrary", "Library of Congress SRU"
    @Operation(summary = "Autocompletar libro externo", 
            description = "Busca un libro por ISBN en Google Books, OpenLibrary y la Library of Congress para extraer metadatos")
    @GetMapping("/autocompletar/{isbn}")
    public ResponseEntity<LibroEnriquecidoDTO> autocompletarLibro(@PathVariable("isbn") String isbn) {

        LibroEnriquecidoDTO libroEnriquecido = orquestadorService.buscarYEnriquecer(isbn);

        //Si el roquestador devolvió null entonces ninguna API logró encontrar el libro
        if (libroEnriquecido == null || libroEnriquecido.getTitulo() == null) {
            return ResponseEntity.notFound().build(); 
        }

        return ResponseEntity.ok(libroEnriquecido);
    }
    @Operation(summary = "Búsqueda filtrada", 
            description = "Busca y retorna los libros que coincidan con los parámetros pasados, se ejecuta una operación AND entre los argumentos")
    @GetMapping("/filtrar")
    public ResponseEntity<Page<LibroCompletoDTO>> buscarLibrosAvanzado(
            @RequestParam(name = "autor", required = false) String autor,
            @RequestParam(name = "categoria",required = false) String categoria,
            @RequestParam(name = "editorial",required = false) String editorial,
            @RequestParam(name = "fechaPub",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPub,
            @RequestParam(name = "codigo",required = false) String codigo,
            Pageable pageable) {
    	Page<LibroCompletoDTO> resultadosDTO = libroService.buscarLibrosAvanzado(autor, categoria, editorial, fechaPub, codigo, pageable);
        return ResponseEntity.ok(resultadosDTO);
    }
}
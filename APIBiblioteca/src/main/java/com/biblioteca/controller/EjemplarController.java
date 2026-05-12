package com.biblioteca.controller;

import java.time.LocalDate;

import org.hibernate.query.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.dto.EjemplarEstadoDTO;
import com.biblioteca.dto.EjemplarLibroAutoresDTO;
import com.biblioteca.dto.EjemplarRegistroDTO;
import com.biblioteca.dto.EjemplarRespuestaDTO;
import com.biblioteca.entity.Ejemplar;
import com.biblioteca.service.EjemplarService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/biblioteca/ejemplares")
@Tag(name = "Catálogo de Ejemplares", description = "Operaciones para gestionar el catálogo de ejemplares")
public class EjemplarController {

    private final LibroController libroController;

	private final EjemplarService ejemplarService;

	public EjemplarController(EjemplarService ejemplarService, LibroController libroController) {
		this.ejemplarService = ejemplarService;
		this.libroController = libroController;
	}

	//Registro de un ejemplar
    @Operation(summary = "Registrar un ejemplar", 
            description = "Dar de alta un ejemplar físico de un libro")
	@PostMapping
	public ResponseEntity<EjemplarRespuestaDTO> registrarEjemplar(@RequestBody EjemplarRegistroDTO dto) {

		Ejemplar ejemplarGuardado = ejemplarService.registrarEjemplar(dto);

		EjemplarRespuestaDTO respuesta = new EjemplarRespuestaDTO(ejemplarGuardado.getId(),
				ejemplarGuardado.getNoAdquisicion(), ejemplarGuardado.getLibro().getIsbn(),
				ejemplarGuardado.getEstado(), ejemplarGuardado.getCondicion(),
				"El ejemplar se ha registrado con éxito.");

		return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
	}
	
	//Consulta de un único ejemplar
    @Operation(summary = "Obtener ejemplar", 
            description = "Buscar y obtener la información del ejemplar y su obra relacionada por su número de adquisición (solo necesaria p. ej. autores, titulo etc)")
	@GetMapping("/{noAdquisicion}")
	public ResponseEntity<EjemplarLibroAutoresDTO> obtenerPorNumeroDeAdquisicion(
			@PathVariable("noAdquisicion") String noAdquisicion) {
		EjemplarLibroAutoresDTO dto = ejemplarService.obtenerDTOLibroAutores(noAdquisicion);
		return ResponseEntity.ok(dto);
	}
	
	//Estado de la entidad "Ejemplar"
    @Operation(summary = "Obtener el estado de la entidad \"ejemplar\"", 
            description = "Obtener un resumen sobre los ejemplares (cantidad, prestados, disponibles)")
	@GetMapping("/estado")
    public ResponseEntity<EjemplarEstadoDTO> obtenerEstadisticasEjemplares() {
		EjemplarEstadoDTO  reporteSeguro = ejemplarService.obtenerEstadoEjemplares();
        return ResponseEntity.ok(reporteSeguro);
    }

}
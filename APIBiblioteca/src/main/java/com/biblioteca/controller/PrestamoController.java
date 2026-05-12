package com.biblioteca.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.biblioteca.dto.EjemplarLibroAutoresDTO;
import com.biblioteca.dto.ExtenderPrestamoDTO;
import com.biblioteca.dto.FinalizarPrestamoRespuestaDTO;
import com.biblioteca.dto.PaginaRespuestaDTO;
import com.biblioteca.dto.PrestamoCompletoDTO;
import com.biblioteca.dto.PrestamoHistorialDTO;
import com.biblioteca.dto.PrestamoRegistroDTO;
import com.biblioteca.dto.PrestamoRespuestaDTO;
import com.biblioteca.dto.PrestamoResumenDTO;
import com.biblioteca.dto.PrestamosEstadoDTO;
import com.biblioteca.entity.Prestamo;
import com.biblioteca.service.PrestamoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/biblioteca/prestamos")
@Tag(name = "Catálogo de Préstamo", description = "Operaciones para gestionar el catálogo de préstamos")
public class PrestamoController {

	
	private final PrestamoService prestamoService;
	
	public PrestamoController(PrestamoService prestamoService) {
		this.prestamoService = prestamoService;
	}
	
	//Realizar un préstamo
    @Operation(summary = "Realizar un préstamo")
	@PostMapping
    public ResponseEntity<PrestamoRespuestaDTO> realizarPrestamo(@RequestBody PrestamoRegistroDTO dto) {
        
        Prestamo prestamoGuardado = prestamoService.registrarPrestamo(dto);
        
        PrestamoRespuestaDTO respuesta = new PrestamoRespuestaDTO(
            prestamoGuardado.getId(),
            prestamoGuardado.getUsuario(),
            prestamoGuardado.getFechaLimite(),
            prestamoGuardado.getCantidadLibrosPrestados()
        );
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
	
	//finalizar un prestamo
    @Operation(summary = "Finalizar un préstamo", 
            description = "Buscar y actualiza la fecha de devolución del préstamo, representa la devolución de ejemplares físicos")
	@PatchMapping("/{id_prestamo}")
	public ResponseEntity<FinalizarPrestamoRespuestaDTO> finalizarPrestamo(@PathVariable("id_prestamo") UUID idPrestamo){
		Prestamo prestamo = prestamoService.finalizarPrestamo(idPrestamo);
		
		FinalizarPrestamoRespuestaDTO respuesta = new FinalizarPrestamoRespuestaDTO(
				prestamo.getId(),
				prestamo.getUsuario(),
				prestamo.getFechaDevolucion(),
				prestamo.getCantidadLibrosPrestados(),
				"El prestamo ha finalizado exitosamente"
				);
		
		return new ResponseEntity<>(respuesta, HttpStatus.OK);
	}
	
	//Extender la fecha limite de un prestamo
    @Operation(summary = "Extender un prestamo", 
            description = "Buscar y actualizar la fecha límite para devolver los recursos")
	@PatchMapping("{id}/{fecha}")
	public ResponseEntity<ExtenderPrestamoDTO> ExtenderPrestamo(
			@PathVariable("id") UUID id,
			@PathVariable("fecha") LocalDate nuevaFecha){
		ExtenderPrestamoDTO dto = prestamoService.extenderPrestamo(id, nuevaFecha); 
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
	//Obtener el historial de un usuario
    @Operation(summary = "Obtener el Historial de un usuario")
	@GetMapping("/usuario/{usuario}")
    public ResponseEntity<List<PrestamoHistorialDTO>> obtenerHistorial(@PathVariable("usuario") String usuario) {
        
        List<PrestamoHistorialDTO> historial = prestamoService.obtenerHistorialUsuario(usuario);
        
        return new ResponseEntity<>(historial, HttpStatus.OK);
    }
	
	//Obtener de manera paginada, con la opcion de elegir el número de página y el tamaño
    @Operation(summary = "Listar los prestamos", 
            description = "Buscar y obtener una página con información resumida sobre los préstamos")
    @GetMapping
    public ResponseEntity<PaginaRespuestaDTO<PrestamoResumenDTO>> listarPrestamosPaginados(
            @RequestParam(defaultValue = "0") int nPage,
            @RequestParam(defaultValue = "10") int len
    ) {
        PaginaRespuestaDTO<PrestamoResumenDTO> catalogo = prestamoService.obtenerCatalogo(nPage, len);
        return ResponseEntity.ok(catalogo);
    }
    
    //Consultar la información completa de un prestamo
    @Operation(summary = "Obtener un prestamo", 
            description = "Buscar y obtener los datos de un prestamo por id")
	@GetMapping("{id}")
    public ResponseEntity <PrestamoCompletoDTO> obtenerPrestamo(@PathVariable("id") UUID id) {
        
        PrestamoCompletoDTO historial = prestamoService.obtenerPrestamo(id);
        
        return new ResponseEntity<>(historial, HttpStatus.OK);
    }
	
	//Obtener el estado de la entidad "Prestamo", el parametro "numeroDeDias" tiene la finalidad de fungir ccomo referencia para considerar
	//a un prestamo cómo "próximo a vencer", si numeroDeDias = 3; entonces todos los prestamos cuya fechaLimite se cumpla dentro de 3 dias (a partir de hoy)
	//serán considerados cómo "próximos a vencer"
    @Operation(summary = "Obtener el estado de la entidad \"prestamo\"", 
            description = "El parámetro \"numeroDeDias\" tiene la finalidad de fungir ccomo referencia para considerar\r\n"
            		+ "	a un prestamo cómo \"próximo a vencer\", si numeroDeDias = 3; entonces todos los prestamos cuya fechaLimite se cumpla dentro de 3 dias (a partir de hoy)\r\n"
            		+ "	serán considerados cómo \"próximos a vencer\"")
	@GetMapping("/estado/{numeroDeDias}")
    public ResponseEntity<PrestamosEstadoDTO> obtenerEstadisticasDeEstadoPrestamos(@PathVariable("numeroDeDias") int numeroDeDias){
		return ResponseEntity.ok(prestamoService.reporteEstado(numeroDeDias));
	}
	
	//Obtiene las líneas de detalle de un préstamo (lista de ejemplares que fueron prestados)
	@GetMapping("/detalle/{id}")
	public ResponseEntity<List<EjemplarLibroAutoresDTO>> obtenerDetallePrestamo(@PathVariable("id") UUID id){
		List<EjemplarLibroAutoresDTO> detalles = prestamoService.ObtenerdetallePrestamo(id);
		return new ResponseEntity<>(detalles, HttpStatus.OK);
	}
}

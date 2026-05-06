package com.biblioteca.controller;

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
import com.biblioteca.dto.FinalizarPrestamoRespuestaDTO;
import com.biblioteca.dto.PaginaRespuestaDTO;
import com.biblioteca.dto.PrestamoCompletoDTO;
import com.biblioteca.dto.PrestamoHistorialDTO;
import com.biblioteca.dto.PrestamoRegistroDTO;
import com.biblioteca.dto.PrestamoRespuestaDTO;
import com.biblioteca.dto.PrestamoResumenDTO;
import com.biblioteca.dto.PrestamosEstadoDTO;
import com.biblioteca.entity.Prestamo;
import com.biblioteca.repository.EditorialRepository;
import com.biblioteca.service.PrestamoService;

@Controller
@RequestMapping("/biblioteca/prestamos")
public class PrestamoController {

    private final EditorialRepository editorialRepository;

	
	private final PrestamoService prestamoService;
	
	public PrestamoController(PrestamoService prestamoService, EditorialRepository editorialRepository) {
		this.prestamoService = prestamoService;
		this.editorialRepository = editorialRepository;
	}
	
	
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
	
	@GetMapping("/usuario/{usuario}")
    public ResponseEntity<List<PrestamoHistorialDTO>> obtenerHistorial(@PathVariable("usuario") String usuario) {
        
        List<PrestamoHistorialDTO> historial = prestamoService.obtenerHistorialUsuario(usuario);
        
        return new ResponseEntity<>(historial, HttpStatus.OK);
    }
	
    @GetMapping
    public ResponseEntity<PaginaRespuestaDTO<PrestamoResumenDTO>> listarPrestamosPaginados(
            @RequestParam(defaultValue = "0") int nPage,
            @RequestParam(defaultValue = "10") int len
    ) {
        PaginaRespuestaDTO<PrestamoResumenDTO> catalogo = prestamoService.obtenerCatalogo(nPage, len);
        return ResponseEntity.ok(catalogo);
    }
    
	@GetMapping("{id}")
    public ResponseEntity <PrestamoCompletoDTO> obtenerPrestamo(@PathVariable("id") UUID id) {
        
        PrestamoCompletoDTO historial = prestamoService.obtenerPrestamo(id);
        
        return new ResponseEntity<>(historial, HttpStatus.OK);
    }
	
	@GetMapping("/estado/{numeroDeDias}")
    public ResponseEntity<PrestamosEstadoDTO> obtenerEstadisticasDeEstadoPrestamos(@PathVariable("numeroDeDias") int numeroDeDias){
		return ResponseEntity.ok(prestamoService.reporteEstado(numeroDeDias));
	}
	
	@GetMapping("/detalle/{id}")
	public ResponseEntity<List<EjemplarLibroAutoresDTO>> obtenerDetallePrestamo(@PathVariable("id") UUID id){
		List<EjemplarLibroAutoresDTO> detalles = prestamoService.ObtenerdetallePrestamo(id);
		return new ResponseEntity<>(detalles, HttpStatus.OK);
	}
}

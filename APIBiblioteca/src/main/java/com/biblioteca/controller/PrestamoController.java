package com.biblioteca.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.biblioteca.dto.FinalizarPrestamoRespuestaDTO;
import com.biblioteca.dto.PrestamoRegistroDTO;
import com.biblioteca.dto.PrestamoRespuestaDTO;
import com.biblioteca.entity.Prestamo;
import com.biblioteca.service.PrestamoService;

@Controller
@RequestMapping("/biblioteca/prestamos")
public class PrestamoController {

	
	private final PrestamoService prestamoService;
	
	public PrestamoController(PrestamoService prestamoService) {
		this.prestamoService = prestamoService;
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
}

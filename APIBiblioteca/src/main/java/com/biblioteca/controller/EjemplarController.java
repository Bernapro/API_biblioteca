package com.biblioteca.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.dto.EjemplarRegistroDTO;
import com.biblioteca.dto.EjemplarRespuestaDTO;
import com.biblioteca.entity.Ejemplar;
import com.biblioteca.service.EjemplarService;

@RestController
@RequestMapping("/biblioteca/ejemplares")
public class EjemplarController {

    private final EjemplarService ejemplarService;

    public EjemplarController(EjemplarService ejemplarService) {
        this.ejemplarService = ejemplarService;
    }

    @PostMapping
    public ResponseEntity<EjemplarRespuestaDTO> registrarEjemplar(@RequestBody EjemplarRegistroDTO dto) {
        
        Ejemplar ejemplarGuardado = ejemplarService.registrarEjemplar(dto);
        
        EjemplarRespuestaDTO respuesta = new EjemplarRespuestaDTO(
            ejemplarGuardado.getId(),
            ejemplarGuardado.getNoAdquisicion(),
            ejemplarGuardado.getLibro().getIsbn(),
            ejemplarGuardado.getEstado(),
            ejemplarGuardado.getCondicion(),
            "El ejemplar se ha registrado con éxito."
        );
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
}
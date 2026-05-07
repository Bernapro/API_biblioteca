package com.biblioteca.service;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.dto.EjemplarEstadoDTO;
import com.biblioteca.dto.EjemplarLibroAutoresDTO;
import com.biblioteca.dto.EjemplarRegistroDTO;
import com.biblioteca.entity.Autor;
import com.biblioteca.entity.Ejemplar;
import com.biblioteca.entity.Libro;
import com.biblioteca.enums.EstadoEjemplar;
import com.biblioteca.errorHandling.Exception.*;
import com.biblioteca.repository.EjemplarRepository;
import com.biblioteca.repository.LibroRepository;

@Service
public class EjemplarService {

	private final EjemplarRepository ejemplarRepository;
	private final LibroRepository libroRepository;

	public EjemplarService(EjemplarRepository ejemplarRepository, LibroRepository libroRepository) {
		this.ejemplarRepository = ejemplarRepository;
		this.libroRepository = libroRepository;
	}

	@Transactional
	public Ejemplar registrarEjemplar(EjemplarRegistroDTO dto) {

		// todavía no me decido si el número de adquisición lo delego a la BD
		// o suponer que ya viene con el libro, por el momento está bien así
		if (ejemplarRepository.existsByNoAdquisicion(dto.noAdquisicion())) {
			throw new IllegalArgumentException(
					"El número de adquisición " + dto.noAdquisicion() + " ya está registrado.");
		}

		// si el libro existe se llena el objeto y se guarda
		// el estado de un ejemplar nuevo es, por obvias razones, disponible
		Libro libroPadre = libroRepository.findById(dto.isbnLibro()).orElseThrow(() -> new ResourceNotFoundException(
				"No se puede registrar el ejemplar. El libro con ISBN " + dto.isbnLibro() + " no existe."));

		Ejemplar nuevoEjemplar = new Ejemplar();
		nuevoEjemplar.setNoAdquisicion(dto.noAdquisicion());
		nuevoEjemplar.setCondicion(dto.condicion());
		nuevoEjemplar.setLibro(libroPadre);

		nuevoEjemplar.setEstado(EstadoEjemplar.DISPONIBLE);

		return ejemplarRepository.save(nuevoEjemplar);
	}
	
	@Transactional(readOnly = true)
	public EjemplarLibroAutoresDTO obtenerDTOLibroAutores(String noAdquisicion) {
		Ejemplar ejem = ejemplarRepository.findByNoAdquisicion(noAdquisicion).orElseThrow(() -> new ResourceNotFoundException(
				"el ejemplar con numero de adquisicion:  " + noAdquisicion + " no existe."));
		Libro lib = ejem.getLibro();
		
		String[] autores = lib.getAutores().stream().map(Autor::getPseudonimo).toArray(String[]::new);

		return new EjemplarLibroAutoresDTO(
				ejem.getId(),
				ejem.getNoAdquisicion(),
				lib.getTitulo(),
				autores,
				ejem.getEstado().equals(EstadoEjemplar.DISPONIBLE)
				);
	}
	
    public EjemplarEstadoDTO obtenerEstadoEjemplares() {
        
        EjemplarEstadoDTO reporte = ejemplarRepository.generarEstadisticasEjemplares();
        
        EjemplarEstadoDTO reporteSeguro = new EjemplarEstadoDTO(
            reporte.totales() != null ? reporte.totales() : 0L,
            reporte.prestados() != null ? reporte.prestados() : 0L,
            reporte.disponibles() != null ? reporte.disponibles() : 0L
        );

        return reporteSeguro;
    }
}

package com.biblioteca.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.biblioteca.dto.PrestamoRegistroDTO;
import com.biblioteca.entity.DetallePrestamo;
import com.biblioteca.entity.Ejemplar;
import com.biblioteca.entity.Prestamo;
import com.biblioteca.enums.EstadoEjemplar;
import com.biblioteca.errorHandling.Exception.*;
import com.biblioteca.repository.EjemplarRepository;
import com.biblioteca.repository.PrestamoRepository;

@Service
public class PrestamoService {

	private final PrestamoRepository prestamoRepository;
	private final EjemplarRepository ejemplarRepository;

	public PrestamoService(PrestamoRepository prestamoRepository, EjemplarRepository ejemplarRepository) {
		this.prestamoRepository = prestamoRepository;
		this.ejemplarRepository = ejemplarRepository;
	}

	@Transactional
	public Prestamo registrarPrestamo(PrestamoRegistroDTO dto) {

		Prestamo prestamo = new Prestamo();
		prestamo.setUsuario(dto.usuario());
		prestamo.setFechaInicio(LocalDate.now());
		prestamo.setFechaLimite(LocalDate.now().plusDays(4));
		Set<DetallePrestamo> detalles = new HashSet<>();

		if (dto.ejemplaresIds().isEmpty()) {
			throw new IllegalArgumentException("No puede registar un préstamo sin libros");
		}

		for (UUID ejemplarId : dto.ejemplaresIds()) {

			Ejemplar ejemplar = ejemplarRepository.findById(ejemplarId)
					.orElseThrow(() -> new ResourceNotFoundException("El ejemplar " + ejemplarId + " no existe"));

			if (ejemplar.getEstado() != EstadoEjemplar.DISPONIBLE) {
				throw new IllegalStateException("El ejemplar " + ejemplar.getNoAdquisicion()
						+ " no se puede prestar. Estado: " + ejemplar.getEstado());
			}

			// al actualizarlo en la memoria tambièn se actualiza en la BD,
			// bueno se encola y se actualiza con la llamada a flush()
			ejemplar.setEstado(EstadoEjemplar.PRESTADO);
			DetallePrestamo detalle = new DetallePrestamo();
			detalle.setPrestamo(prestamo);
			detalle.setEjemplar(ejemplar);
			detalles.add(detalle);
		}

		prestamo.setDetalles(detalles);

		// puse cascadeType.ALL para que se ejecute automàticamente la inserciòn
		// de las clases dependientes
		return prestamoRepository.save(prestamo);

	}

	@Transactional
	public Prestamo finalizarPrestamo(UUID idPrestamo) {

		Prestamo prestamo = prestamoRepository.findById(idPrestamo)
				.orElseThrow(() -> new ResourceNotFoundException("El prestamo " + idPrestamo + " no existe."));

		if (prestamo.getFechaDevolucion() != null) {
			throw new IllegalStateException("el prestamo " + idPrestamo + " ya fue finalizado");
		}

		// todo esto se actualiza en la BD automáticamente (dirtyChecking)
		prestamo.setFechaDevolucion(LocalDate.now());

		for (DetallePrestamo det : prestamo.getDetalles()) {
			Ejemplar ejemplar = det.getEjemplar();
			if (ejemplar.getEstado() == EstadoEjemplar.PRESTADO) {
				ejemplar.setEstado(EstadoEjemplar.DISPONIBLE);
			}
		}

		return prestamo;
	}
}
package com.biblioteca.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.biblioteca.dto.PaginaRespuestaDTO;
import com.biblioteca.dto.PrestamoCompletoDTO;
import com.biblioteca.dto.PrestamoHistorialDTO;
import com.biblioteca.dto.PrestamoRegistroDTO;
import com.biblioteca.dto.PrestamoResumenDTO;
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

	@Transactional(readOnly = true)
	public List<PrestamoHistorialDTO> obtenerHistorialUsuario(String usuario) {

		List<Prestamo> historial = prestamoRepository.obtenerHistorialPorUsuario(usuario);

		return historial.stream().map(prestamo -> {
			
			long diasAtraso = prestamo.diasDeAtraso();

			String estado = prestamo.fueDevuelto() ? "finalizado" : diasAtraso > 0 ? "Atrasado" : "A tiempo";

			return new PrestamoHistorialDTO(prestamo.getId(), prestamo.getFechaInicio(), prestamo.getFechaLimite(),
					prestamo.getFechaDevolucion(), prestamo.getCantidadLibrosPrestados(), estado, diasAtraso);
		}).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public PaginaRespuestaDTO<PrestamoResumenDTO> obtenerCatalogo(int numeroPagina, int tamanoPagina) {

		Pageable peticionPagina = PageRequest.of(numeroPagina, tamanoPagina);

		Page<PrestamoResumenDTO> paginaPrestamos = prestamoRepository.obtenerCatalogoResumido(peticionPagina);

		return new PaginaRespuestaDTO<>(paginaPrestamos.getContent(), paginaPrestamos.getNumber(),
				paginaPrestamos.getSize(), paginaPrestamos.getTotalElements(), paginaPrestamos.getTotalPages(),
				paginaPrestamos.isLast());
	}

	@Transactional(readOnly = true)
	public PrestamoCompletoDTO obtenerPrestamo(UUID id) {
		Optional<Prestamo> prestamoOpt = prestamoRepository.findById(id);
		Prestamo prestamo = prestamoOpt.get();
		if (prestamo == null) {
			return null;
		}
		long diasAtraso = prestamo.diasDeAtraso();
		String estado = prestamo.fueDevuelto() ? "finalizado" : diasAtraso > 0 ? "Atrasado" : "A tiempo";

		return new PrestamoCompletoDTO(prestamo.getId(), prestamo.getUsuario(),prestamo.getFechaInicio(), prestamo.getFechaLimite(),
				prestamo.getFechaDevolucion(), prestamo.getCantidadLibrosPrestados(), estado, diasAtraso);

	}
}
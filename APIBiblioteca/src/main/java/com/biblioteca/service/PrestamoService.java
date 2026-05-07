package com.biblioteca.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.dto.EjemplarLibroAutoresDTO;
import com.biblioteca.dto.PaginaRespuestaDTO;
import com.biblioteca.dto.PrestamoCompletoDTO;
import com.biblioteca.dto.PrestamoHistorialDTO;
import com.biblioteca.dto.PrestamoRegistroDTO;
import com.biblioteca.dto.PrestamoResumenDTO;
import com.biblioteca.dto.PrestamosEstadoDTO;
import com.biblioteca.entity.Autor;
import com.biblioteca.entity.DetallePrestamo;
import com.biblioteca.entity.Ejemplar;
import com.biblioteca.entity.Libro;
import com.biblioteca.entity.Prestamo;
import com.biblioteca.enums.EstadoEjemplar;
import com.biblioteca.errorHandling.Exception.*;
import com.biblioteca.repository.EjemplarRepository;
import com.biblioteca.repository.PrestamoRepository;
import com.biblioteca.dto.ExtenderPrestamoDTO;

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
		System.out.println(dto.fechaLimite());
		prestamo.setFechaLimite(dto.fechaLimite());
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
	
	@Transactional
    public ExtenderPrestamoDTO extenderPrestamo(UUID idPrestamo, LocalDate fechaNueva) {
        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new ResourceNotFoundException("El prestamo no existe"));

        LocalDate fechaAntigua = prestamo.getFechaLimite();
        
        if (prestamo.fueDevuelto()) {
            return new ExtenderPrestamoDTO(fechaAntigua, fechaAntigua, "El prestamo ya fue devuelto");
        }
        
        if (!fechaNueva.isAfter(fechaAntigua)) {
            return new ExtenderPrestamoDTO(fechaAntigua, fechaAntigua, "La nueva fecha debe ser posterior a la actual");
        }

        prestamo.setFechaLimite(fechaNueva);


        return new ExtenderPrestamoDTO(
                fechaAntigua,
                fechaNueva,
                "Actualización exitosa"
        );
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

		return new PrestamoCompletoDTO(prestamo.getId(), prestamo.getUsuario(), prestamo.getFechaInicio(),
				prestamo.getFechaLimite(), prestamo.getFechaDevolucion(), prestamo.getCantidadLibrosPrestados(), estado,
				diasAtraso);

	}

	@Transactional(readOnly = true)
	public PrestamosEstadoDTO reporteEstado(int procimoAVencer) {
		LocalDate fechaProxima = LocalDate.now().plusDays(procimoAVencer);
		PrestamosEstadoDTO reporte = prestamoRepository.generarEstadisticasDeEstado(fechaProxima);
		return new PrestamosEstadoDTO(reporte.totales() != null ? reporte.totales() : 0L,
				reporte.vigentes() != null ? reporte.vigentes() : 0L,
				reporte.vencidos() != null ? reporte.vencidos() : 0L,
				reporte.proximosAVencer() != null ? reporte.proximosAVencer() : 0L);
	}

	@Transactional(readOnly = true)
	public List<EjemplarLibroAutoresDTO> ObtenerdetallePrestamo(UUID id) {
		Prestamo prestamo = prestamoRepository.obtenerPrestamoConDetallesCompletos(id)
				.orElseThrow(() -> new ResourceNotFoundException("El prestamo " + id + " no existe"));
		Set<DetallePrestamo> detalles = prestamo.getDetalles();

		if (detalles.isEmpty()) {
			return Collections.emptyList();
		}

		return detalles.stream().map(detalle -> {
			Ejemplar ejemplar = detalle.getEjemplar();
			Libro libro = ejemplar.getLibro();

			String[] autoresArray = libro.getAutores() != null ? libro.getAutores().stream().map(Autor::getPseudonimo).toArray(String[]::new)
					: new String[0];

			return new EjemplarLibroAutoresDTO(ejemplar.getId(), ejemplar.getNoAdquisicion(), libro.getTitulo(),
					autoresArray, ejemplar.getEstado() == EstadoEjemplar.DISPONIBLE? true: false);
		}).toList();

	}
}
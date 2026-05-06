package com.biblioteca.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biblioteca.dto.PrestamoResumenDTO;
import com.biblioteca.dto.PrestamosEstadoDTO;
import com.biblioteca.entity.Prestamo;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, UUID> {

	@Query("SELECT DISTINCT p FROM Prestamo p LEFT JOIN FETCH p.detalles WHERE p.usuario = :usuario ORDER BY p.fechaInicio DESC")
	List<Prestamo> obtenerHistorialPorUsuario(@Param("usuario") String usuario);

	@Query("SELECT new com.biblioteca.dto.PrestamoResumenDTO(p.id, p.usuario, "
			+ "p.fechaInicio, p.fechaLimite, p.fechaDevolucion, COUNT(d)) " + "FROM Prestamo p "
			+ "LEFT JOIN p.detalles d " + "GROUP BY p.id, p.usuario, p.fechaInicio, p.fechaLimite, p.fechaDevolucion")
	Page<PrestamoResumenDTO> obtenerCatalogoResumido(Pageable pageable);

	@Query("SELECT new com.biblioteca.dto.PrestamosEstadoDTO(" + "COUNT(p), "
			+ "SUM(CASE WHEN p.fechaDevolucion IS NULL THEN 1L ELSE 0L END), "
			+ "SUM(CASE WHEN p.fechaDevolucion IS NULL AND p.fechaLimite < CURRENT_DATE THEN 1L ELSE 0L END), "
			+ "SUM(CASE WHEN p.fechaDevolucion IS NULL AND p.fechaLimite >= CURRENT_DATE AND p.fechaLimite <= :fechaProxima THEN 1L ELSE 0L END)) "
			+ "FROM Prestamo p")
	PrestamosEstadoDTO generarEstadisticasDeEstado(@Param("fechaProxima") LocalDate fechaProxima);
}

package com.biblioteca.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biblioteca.dto.PrestamoResumenDTO;
import com.biblioteca.entity.Prestamo;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, UUID> {
	
	@Query("SELECT DISTINCT p FROM Prestamo p LEFT JOIN FETCH p.detalles WHERE p.usuario = :usuario ORDER BY p.fechaInicio DESC")
    List<Prestamo> obtenerHistorialPorUsuario(@Param("usuario") String usuario);
	
	@Query("SELECT new com.biblioteca.dto.PrestamoResumenDTO(p.id, p.usuario,"
			+ "p.fechaInicio, p.fechaLimite, p.fechaDevolucion) FROM Prestamo p")
	Page<PrestamoResumenDTO> obtenerCatalogoResumido(Pageable pageable);
}

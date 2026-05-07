package com.biblioteca.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.biblioteca.dto.EjemplarEstadoDTO;
import com.biblioteca.entity.Ejemplar;

@Repository
public interface EjemplarRepository extends JpaRepository<Ejemplar, UUID> {

	List<Ejemplar> findByLibroIsbn(String isbn);

	boolean existsByNoAdquisicion(String noAdquisicion);

	Optional<Ejemplar> findByNoAdquisicion(String noAdquisicion);

	long countByLibroIsbn(String isbn);

	@Query(value = "SELECT nextval('seq_no_adquisicion')", nativeQuery = true)
	Long obtenerSiguienteSecuenciaAdquisicion();

	@Query("SELECT new com.biblioteca.dto.EjemplarEstadoDTO(" + "COUNT(e), "
			+ "SUM(CASE WHEN e.estado = 'PRESTADO' THEN 1L ELSE 0L END), "
			+ "SUM(CASE WHEN e.estado = 'DISPONIBLE' THEN 1L ELSE 0L END)) " + "FROM Ejemplar e")
	EjemplarEstadoDTO generarEstadisticasEjemplares();
}

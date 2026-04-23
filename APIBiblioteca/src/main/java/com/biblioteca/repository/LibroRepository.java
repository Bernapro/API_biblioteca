package com.biblioteca.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biblioteca.dto.LibroResumenDTO;
import com.biblioteca.entity.Libro;

@Repository
public interface LibroRepository extends JpaRepository<Libro, String> {

	@Query("SELECT new com.biblioteca.dto.LibroResumenDTO(l.isbn, l.titulo, e.nombre) "
			+ "FROM Libro l JOIN l.editorial e")
	List<LibroResumenDTO> obtenerCatalogoResumido();
	
	@Query("SELECT l FROM Libro l LEFT JOIN FETCH l.editorial WHERE l.isbn = :isbn")
    Optional<Libro> obtenerLibroConEditorial(@Param("isbn") String isbn);
}

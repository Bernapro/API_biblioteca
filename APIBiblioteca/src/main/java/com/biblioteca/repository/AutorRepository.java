package com.biblioteca.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biblioteca.entity.Autor;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {

	@Query(value = "SELECT * FROM autor a " + "WHERE lower(unaccent(replace(a.pseudonimo, ' ', ''))) = "
			+ "      lower(unaccent(replace(:nombreIngresado, ' ', ''))) " + "LIMIT 1", nativeQuery = true)
	Optional<Autor> encontrarAutorNormalizado(@Param("nombreIngresado") String nombreIngresado);
}

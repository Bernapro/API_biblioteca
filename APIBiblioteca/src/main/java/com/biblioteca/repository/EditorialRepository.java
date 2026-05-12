package com.biblioteca.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biblioteca.entity.Editorial;

@Repository
public interface EditorialRepository extends JpaRepository<Editorial, Long> {

	@Query(value = "SELECT * FROM editorial e " + "WHERE lower(unaccent(replace(e.editorial, ' ', ''))) = "
			+ "      lower(f_unaccent(replace(:nombreIngresado, ' ', ''))) " + "LIMIT 1", nativeQuery = true)
	Optional<Editorial> encontrarEditorialNormalizada(@Param("nombreIngresado") String nombreIngresado);
}
package com.biblioteca.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biblioteca.entity.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

	@Query(value = "SELECT * FROM categoria c " + "WHERE lower(unaccent(replace(c.categoria, ' ', ''))) = "
			+ "      lower(f_unaccent(replace(:nombreIngresado, ' ', ''))) " + "LIMIT 1", nativeQuery = true)
	Optional<Categoria> encontrarCategoriaNormalizada(@Param("nombreIngresado") String nombreIngresado);

}

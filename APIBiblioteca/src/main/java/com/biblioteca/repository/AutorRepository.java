package com.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.entity.Autor;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
	
		
}

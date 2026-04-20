package com.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.entity.Pais;


@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {

}

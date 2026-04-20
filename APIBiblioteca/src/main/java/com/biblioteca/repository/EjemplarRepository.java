package com.biblioteca.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.entity.Ejemplar;

@Repository
public interface EjemplarRepository extends JpaRepository<Ejemplar, UUID> {

}

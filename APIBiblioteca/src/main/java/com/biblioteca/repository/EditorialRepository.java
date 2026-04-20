package com.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.entity.Editorial;

@Repository
public interface EditorialRepository extends JpaRepository<Editorial, Long> {

}
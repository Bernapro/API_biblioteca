package com.biblioteca.repository; 

import com.biblioteca.entity.Autor;
import com.biblioteca.entity.Categoria;
import com.biblioteca.entity.Editorial;
import com.biblioteca.entity.Libro;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibroSpecification {

    public static Specification<Libro> buscarConFiltros(
            String autor, String categoria, String editorial, 
            LocalDate fechaPub, String codigoClasificacion) {

        return (root, query, cb) -> {
            query.distinct(true);
            
            List<Predicate> predicados = new ArrayList<>();

            if (autor != null && !autor.isBlank()) {
                Join<Libro, Autor> autoresJoin = root.join("autores");
                predicados.add(cb.like(cb.lower(autoresJoin.get("pseudonimo")), "%" + autor.toLowerCase() + "%"));
            }

            if (categoria != null && !categoria.isBlank()) {
                Join<Libro, Categoria> categoriasJoin = root.join("categorias");
                predicados.add(cb.like(cb.lower(categoriasJoin.get("nombre")), "%" + categoria.toLowerCase() + "%"));
            }

            if (editorial != null && !editorial.isBlank()) {
                Join<Libro, Editorial> editorialJoin = root.join("editorial");
                predicados.add(cb.like(cb.lower(editorialJoin.get("nombre")), "%" + editorial.toLowerCase() + "%"));
            }

            if (fechaPub != null) {
                predicados.add(cb.equal(root.get("fechaPublicacion"), fechaPub));
            }

            if (codigoClasificacion != null && !codigoClasificacion.isBlank()) {
                String codigo = "%" + codigoClasificacion.toLowerCase() + "%";
                Predicate congreso = cb.like(cb.lower(root.get("clasificacionDelCongreso")), codigo);
                Predicate dewey = cb.like(cb.lower(root.get("dewey")), codigo);
                Predicate decimalUniv = cb.like(cb.lower(root.get("clasificacionDecimalUniversal")), codigo);
                
                predicados.add(cb.or(congreso, dewey, decimalUniv));
            }

            return cb.and(predicados.toArray(new Predicate[0]));
        };
    }
}
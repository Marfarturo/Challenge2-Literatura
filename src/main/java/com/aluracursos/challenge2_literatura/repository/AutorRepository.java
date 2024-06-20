package com.aluracursos.challenge2_literatura.repository;

import com.aluracursos.challenge2_literatura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findByFechaMuerteGreaterThan(Integer fechaMuerte);
}

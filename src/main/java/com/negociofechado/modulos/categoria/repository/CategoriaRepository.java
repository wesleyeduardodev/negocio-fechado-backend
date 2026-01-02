package com.negociofechado.modulos.categoria.repository;

import com.negociofechado.modulos.categoria.entity.Categoria;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByAtivoTrueOrderByNomeAsc();

}

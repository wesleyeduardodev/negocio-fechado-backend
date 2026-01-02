package com.negociofechado.modulos.categoria.service;

import com.negociofechado.modulos.categoria.dto.CategoriaResponse;
import com.negociofechado.modulos.categoria.entity.Categoria;
import com.negociofechado.modulos.categoria.repository.CategoriaRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaResponse> listarAtivas() {
        return categoriaRepository.findByAtivoTrueOrderByNomeAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getIcone()
        );
    }

}

package com.negociofechado.modulos.categoria.controller;

import com.negociofechado.modulos.categoria.document.CategoriaDocument;
import com.negociofechado.modulos.categoria.dto.CategoriaResponse;
import com.negociofechado.modulos.categoria.service.CategoriaService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController implements CategoriaDocument {

    private final CategoriaService categoriaService;

    @Override
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        List<CategoriaResponse> categorias = categoriaService.listarAtivas();
        return ResponseEntity.ok(categorias);
    }

}

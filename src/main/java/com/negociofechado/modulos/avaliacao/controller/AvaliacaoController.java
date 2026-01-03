package com.negociofechado.modulos.avaliacao.controller;

import com.negociofechado.modulos.avaliacao.document.AvaliacaoDocument;
import com.negociofechado.modulos.avaliacao.dto.AvaliacaoRequest;
import com.negociofechado.modulos.avaliacao.dto.AvaliacaoResponse;
import com.negociofechado.modulos.avaliacao.service.AvaliacaoService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController implements AvaliacaoDocument {

    private final AvaliacaoService avaliacaoService;

    @Override
    @PostMapping("/solicitacao/{solicitacaoId}")
    public ResponseEntity<AvaliacaoResponse> criar(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long solicitacaoId,
            @Valid @RequestBody AvaliacaoRequest request) {
        AvaliacaoResponse response = avaliacaoService.criar(usuarioId, solicitacaoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/profissional/{profissionalId}")
    public ResponseEntity<Page<AvaliacaoResponse>> listarPorProfissional(
            @PathVariable Long profissionalId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<AvaliacaoResponse> response = avaliacaoService.listarPorProfissional(profissionalId, pageable);
        return ResponseEntity.ok(response);
    }

}

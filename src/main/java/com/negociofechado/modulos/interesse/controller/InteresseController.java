package com.negociofechado.modulos.interesse.controller;

import com.negociofechado.modulos.interesse.dto.CriarInteresseRequest;
import com.negociofechado.modulos.interesse.dto.InteresseResponse;
import com.negociofechado.modulos.interesse.dto.ProfissionalStatsResponse;
import com.negociofechado.modulos.interesse.service.InteresseService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/interesses")
@RequiredArgsConstructor
public class InteresseController {

    private final InteresseService interesseService;

    @PostMapping
    public ResponseEntity<InteresseResponse> criar(
            @AuthenticationPrincipal Long usuarioId,
            @Valid @RequestBody CriarInteresseRequest request) {
        InteresseResponse response = interesseService.criar(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/solicitacao/{solicitacaoId}")
    public ResponseEntity<List<InteresseResponse>> listarPorSolicitacao(
            @AuthenticationPrincipal Long clienteId,
            @PathVariable Long solicitacaoId) {
        List<InteresseResponse> response = interesseService.listarPorSolicitacao(clienteId, solicitacaoId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{interesseId}/visualizar")
    public ResponseEntity<Void> marcarComoVisualizado(
            @AuthenticationPrincipal Long clienteId,
            @PathVariable Long interesseId) {
        interesseService.marcarComoVisualizado(clienteId, interesseId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{interesseId}/contratar")
    public ResponseEntity<Void> marcarComoContratado(
            @AuthenticationPrincipal Long clienteId,
            @PathVariable Long interesseId) {
        interesseService.marcarComoContratado(clienteId, interesseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<ProfissionalStatsResponse> getStatsProfissional(
            @AuthenticationPrincipal Long usuarioId) {
        ProfissionalStatsResponse response = interesseService.getStatsProfissional(usuarioId);
        return ResponseEntity.ok(response);
    }
}

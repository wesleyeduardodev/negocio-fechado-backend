package com.negociofechado.modulos.solicitacao.controller;

import com.negociofechado.modulos.solicitacao.document.SolicitacaoDocument;
import com.negociofechado.modulos.solicitacao.dto.CriarSolicitacaoRequest;
import com.negociofechado.modulos.solicitacao.dto.SolicitacaoDetalheResponse;
import com.negociofechado.modulos.solicitacao.dto.SolicitacaoResumoResponse;
import com.negociofechado.modulos.solicitacao.dto.SolicitacoesStatsResponse;
import com.negociofechado.modulos.solicitacao.service.SolicitacaoService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController implements SolicitacaoDocument {

    private final SolicitacaoService solicitacaoService;

    @Override
    @PostMapping
    public ResponseEntity<SolicitacaoDetalheResponse> criar(
            @AuthenticationPrincipal Long usuarioId,
            @Valid @RequestBody CriarSolicitacaoRequest request) {
        SolicitacaoDetalheResponse response = solicitacaoService.criar(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<SolicitacaoResumoResponse>> listar(
            @AuthenticationPrincipal Long usuarioId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<SolicitacaoResumoResponse> response = solicitacaoService.listarPorCliente(usuarioId, pageable);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoDetalheResponse> buscarPorId(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long id) {
        SolicitacaoDetalheResponse response = solicitacaoService.buscarPorId(usuarioId, id);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long id) {
        solicitacaoService.cancelar(usuarioId, id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/stats")
    public ResponseEntity<SolicitacoesStatsResponse> stats(
            @AuthenticationPrincipal Long usuarioId) {
        SolicitacoesStatsResponse response = solicitacaoService.getStats(usuarioId);
        return ResponseEntity.ok(response);
    }

}

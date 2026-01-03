package com.negociofechado.modulos.orcamento.controller;

import com.negociofechado.modulos.orcamento.document.OrcamentoDocument;
import com.negociofechado.modulos.orcamento.dto.OrcamentoEnviadoResponse;
import com.negociofechado.modulos.orcamento.dto.OrcamentoRequest;
import com.negociofechado.modulos.orcamento.dto.OrcamentoResumoResponse;
import com.negociofechado.modulos.orcamento.service.OrcamentoService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/orcamentos")
@RequiredArgsConstructor
public class OrcamentoController implements OrcamentoDocument {

    private final OrcamentoService orcamentoService;

    @Override
    @PostMapping("/solicitacao/{solicitacaoId}")
    public ResponseEntity<OrcamentoResumoResponse> enviar(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long solicitacaoId,
            @Valid @RequestBody OrcamentoRequest request) {
        OrcamentoResumoResponse response = orcamentoService.enviar(usuarioId, solicitacaoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/solicitacao/{solicitacaoId}")
    public ResponseEntity<List<OrcamentoResumoResponse>> listarPorSolicitacao(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long solicitacaoId) {
        List<OrcamentoResumoResponse> response = orcamentoService.listarPorSolicitacao(usuarioId, solicitacaoId);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/enviados")
    public ResponseEntity<Page<OrcamentoEnviadoResponse>> listarEnviados(
            @AuthenticationPrincipal Long usuarioId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<OrcamentoEnviadoResponse> response = orcamentoService.listarEnviados(usuarioId, pageable);
        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/{id}/aceitar")
    public ResponseEntity<Void> aceitar(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long id) {
        orcamentoService.aceitar(usuarioId, id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/{id}/recusar")
    public ResponseEntity<Void> recusar(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long id) {
        orcamentoService.recusar(usuarioId, id);
        return ResponseEntity.noContent().build();
    }
}

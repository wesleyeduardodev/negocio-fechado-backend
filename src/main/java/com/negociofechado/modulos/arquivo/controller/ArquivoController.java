package com.negociofechado.modulos.arquivo.controller;

import com.negociofechado.modulos.arquivo.document.ArquivoDocument;
import com.negociofechado.modulos.arquivo.dto.ArquivoResponse;
import com.negociofechado.modulos.arquivo.service.ArquivoService;
import com.negociofechado.modulos.solicitacao.service.SolicitacaoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/arquivos")
@RequiredArgsConstructor
public class ArquivoController implements ArquivoDocument {

    private final ArquivoService arquivoService;
    private final SolicitacaoService solicitacaoService;

    @Override
    @PostMapping("/solicitacoes/{solicitacaoId}/fotos")
    public ResponseEntity<List<ArquivoResponse>> uploadFotosSolicitacao(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long solicitacaoId,
            @RequestParam("fotos") List<MultipartFile> fotos) {

        solicitacaoService.verificarProprietario(solicitacaoId, usuarioId);

        List<ArquivoResponse> response = arquivoService.uploadFotosSolicitacao(solicitacaoId, fotos);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/solicitacoes/{solicitacaoId}/fotos")
    public ResponseEntity<List<ArquivoResponse>> listarFotosSolicitacao(
            @PathVariable Long solicitacaoId) {

        List<ArquivoResponse> response = arquivoService.listarFotosSolicitacao(solicitacaoId);

        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/solicitacoes/{solicitacaoId}/fotos/{fotoId}")
    public ResponseEntity<Void> deletarFotoSolicitacao(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long solicitacaoId,
            @PathVariable Long fotoId) {

        solicitacaoService.verificarProprietario(solicitacaoId, usuarioId);

        arquivoService.deletarFoto(fotoId);

        return ResponseEntity.noContent().build();
    }
}

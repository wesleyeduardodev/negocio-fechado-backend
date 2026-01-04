package com.negociofechado.modulos.arquivo.controller;

import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.modulos.arquivo.document.ArquivoDocument;
import com.negociofechado.modulos.arquivo.dto.ArquivoResponse;
import com.negociofechado.modulos.avaliacao.service.AvaliacaoFotoService;
import com.negociofechado.modulos.solicitacao.service.SolicitacaoFotoService;
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

    private final SolicitacaoFotoService solicitacaoFotoService;
    private final SolicitacaoService solicitacaoService;
    private final AvaliacaoFotoService avaliacaoFotoService;

    @Override
    @PostMapping("/solicitacoes/{solicitacaoId}/fotos")
    public ResponseEntity<List<ArquivoResponse>> uploadFotosSolicitacao(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long solicitacaoId,
            @RequestParam("fotos") List<MultipartFile> fotos) {

        solicitacaoService.verificarProprietario(solicitacaoId, usuarioId);

        List<ArquivoResponse> response = solicitacaoFotoService.uploadFotos(solicitacaoId, fotos);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/solicitacoes/{solicitacaoId}/fotos")
    public ResponseEntity<List<ArquivoResponse>> listarFotosSolicitacao(
            @PathVariable Long solicitacaoId) {

        List<ArquivoResponse> response = solicitacaoFotoService.listarFotos(solicitacaoId);

        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/solicitacoes/{solicitacaoId}/fotos/{fotoId}")
    public ResponseEntity<Void> deletarFotoSolicitacao(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long solicitacaoId,
            @PathVariable Long fotoId) {

        solicitacaoService.verificarProprietario(solicitacaoId, usuarioId);

        solicitacaoFotoService.deletarFoto(fotoId);

        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/avaliacoes/{avaliacaoId}/fotos")
    public ResponseEntity<List<ArquivoResponse>> uploadFotosAvaliacao(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long avaliacaoId,
            @RequestParam("fotos") List<MultipartFile> fotos) {

        Long clienteId = avaliacaoFotoService.getAvaliacaoClienteId(avaliacaoId);
        if (!clienteId.equals(usuarioId)) {
            throw new NegocioException("Voce nao tem permissao para adicionar fotos nesta avaliacao");
        }

        List<ArquivoResponse> response = avaliacaoFotoService.uploadFotos(avaliacaoId, fotos);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/avaliacoes/{avaliacaoId}/fotos")
    public ResponseEntity<List<ArquivoResponse>> listarFotosAvaliacao(
            @PathVariable Long avaliacaoId) {

        List<ArquivoResponse> response = avaliacaoFotoService.listarFotos(avaliacaoId);

        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/avaliacoes/{avaliacaoId}/fotos/{fotoId}")
    public ResponseEntity<Void> deletarFotoAvaliacao(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long avaliacaoId,
            @PathVariable Long fotoId) {

        Long clienteId = avaliacaoFotoService.getAvaliacaoClienteId(avaliacaoId);
        if (!clienteId.equals(usuarioId)) {
            throw new NegocioException("Voce nao tem permissao para deletar fotos desta avaliacao");
        }

        avaliacaoFotoService.deletarFoto(fotoId);

        return ResponseEntity.noContent().build();
    }
}

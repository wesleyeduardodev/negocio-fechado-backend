package com.negociofechado.modulos.arquivo.document;

import com.negociofechado.modulos.arquivo.dto.ArquivoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Arquivos", description = "Endpoints para upload e gerenciamento de arquivos")
public interface ArquivoDocument {

    @Operation(summary = "Upload de fotos para solicitacao")
    @ApiResponse(responseCode = "201", description = "Fotos enviadas com sucesso")
    @ApiResponse(responseCode = "400", description = "Limite de fotos excedido ou arquivo invalido")
    @PostMapping("/solicitacoes/{solicitacaoId}/fotos")
    ResponseEntity<List<ArquivoResponse>> uploadFotosSolicitacao(
        @AuthenticationPrincipal Long usuarioId,
        @PathVariable Long solicitacaoId,
        @Parameter(description = "Fotos (max 5, JPG/PNG/WebP, max 10MB cada)")
        @RequestParam("fotos") List<MultipartFile> fotos
    );

    @Operation(summary = "Listar fotos de uma solicitacao")
    @ApiResponse(responseCode = "200", description = "Lista de fotos")
    @GetMapping("/solicitacoes/{solicitacaoId}/fotos")
    ResponseEntity<List<ArquivoResponse>> listarFotosSolicitacao(
        @PathVariable Long solicitacaoId
    );

    @Operation(summary = "Deletar uma foto da solicitacao")
    @ApiResponse(responseCode = "204", description = "Foto deletada com sucesso")
    @ApiResponse(responseCode = "404", description = "Foto nao encontrada")
    @DeleteMapping("/solicitacoes/{solicitacaoId}/fotos/{fotoId}")
    ResponseEntity<Void> deletarFotoSolicitacao(
        @AuthenticationPrincipal Long usuarioId,
        @PathVariable Long solicitacaoId,
        @PathVariable Long fotoId
    );
}

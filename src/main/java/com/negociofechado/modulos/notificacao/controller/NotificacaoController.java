package com.negociofechado.modulos.notificacao.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.negociofechado.modulos.notificacao.dto.NotificacaoResponse;
import com.negociofechado.modulos.notificacao.dto.RegistrarTokenRequest;
import com.negociofechado.modulos.notificacao.service.NotificacaoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController implements NotificacaoDocument {

    private final NotificacaoService notificacaoService;

    @Override
    @PostMapping("/token")
    public ResponseEntity<Void> registrarToken(
            @AuthenticationPrincipal Long usuarioId,
            @Valid @RequestBody RegistrarTokenRequest request) {

        notificacaoService.registrarToken(usuarioId, request);
        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping("/token/{token}")
    public ResponseEntity<Void> removerToken(@PathVariable String token) {
        notificacaoService.removerToken(token);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping
    public ResponseEntity<List<NotificacaoResponse>> listar(
            @AuthenticationPrincipal Long usuarioId) {

        List<NotificacaoResponse> notificacoes = notificacaoService.listar(usuarioId);
        return ResponseEntity.ok(notificacoes);
    }

    @Override
    @GetMapping("/nao-lidas/count")
    public ResponseEntity<Map<String, Long>> contarNaoLidas(
            @AuthenticationPrincipal Long usuarioId) {

        long count = notificacaoService.contarNaoLidas(usuarioId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @Override
    @PutMapping("/{notificacaoId}/lida")
    public ResponseEntity<Void> marcarComoLida(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long notificacaoId) {

        notificacaoService.marcarComoLida(notificacaoId, usuarioId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PutMapping("/lidas")
    public ResponseEntity<Void> marcarTodasComoLidas(
            @AuthenticationPrincipal Long usuarioId) {

        notificacaoService.marcarTodasComoLidas(usuarioId);
        return ResponseEntity.ok().build();
    }
}

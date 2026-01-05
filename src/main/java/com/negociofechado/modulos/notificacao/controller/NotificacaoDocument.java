package com.negociofechado.modulos.notificacao.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.negociofechado.modulos.notificacao.dto.NotificacaoResponse;
import com.negociofechado.modulos.notificacao.dto.RegistrarTokenRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Notificacoes", description = "Gerenciamento de notificacoes push")
public interface NotificacaoDocument {

    @Operation(summary = "Registrar token do dispositivo", description = "Registra o token push do dispositivo do usuario")
    ResponseEntity<Void> registrarToken(Long usuarioId, RegistrarTokenRequest request);

    @Operation(summary = "Remover token do dispositivo", description = "Remove/desativa o token push do dispositivo")
    ResponseEntity<Void> removerToken(String token);

    @Operation(summary = "Listar notificacoes", description = "Lista todas as notificacoes do usuario")
    ResponseEntity<List<NotificacaoResponse>> listar(Long usuarioId);

    @Operation(summary = "Contar nao lidas", description = "Retorna a quantidade de notificacoes nao lidas")
    ResponseEntity<Map<String, Long>> contarNaoLidas(Long usuarioId);

    @Operation(summary = "Marcar como lida", description = "Marca uma notificacao especifica como lida")
    ResponseEntity<Void> marcarComoLida(Long usuarioId, Long notificacaoId);

    @Operation(summary = "Marcar todas como lidas", description = "Marca todas as notificacoes do usuario como lidas")
    ResponseEntity<Void> marcarTodasComoLidas(Long usuarioId);
}

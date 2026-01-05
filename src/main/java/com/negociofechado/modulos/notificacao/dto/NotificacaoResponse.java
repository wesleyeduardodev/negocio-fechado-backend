package com.negociofechado.modulos.notificacao.dto;

import java.time.LocalDateTime;

import com.negociofechado.modulos.notificacao.entity.TipoNotificacao;

public record NotificacaoResponse(
    Long id,
    TipoNotificacao tipo,
    String titulo,
    String corpo,
    Long referenciaId,
    Boolean lida,
    LocalDateTime criadoEm
) {}

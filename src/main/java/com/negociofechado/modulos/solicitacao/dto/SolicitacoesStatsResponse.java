package com.negociofechado.modulos.solicitacao.dto;

public record SolicitacoesStatsResponse(
        long total,
        long abertas,
        long emAndamento,
        long concluidas
) {}

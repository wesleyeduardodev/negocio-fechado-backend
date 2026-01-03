package com.negociofechado.modulos.orcamento.dto;

public record ProfissionalStatsResponse(
    long orcamentosEnviados,
    long emNegociacao,
    long finalizados
) {}

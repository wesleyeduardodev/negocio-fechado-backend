package com.negociofechado.modulos.solicitacao.dto;

import java.time.LocalDateTime;

public record SolicitacaoResumoResponse(
        Long id,
        String titulo,
        String categoriaNome,
        String categoriaIcone,
        String status,
        String cidadeNome,
        String uf,
        LocalDateTime criadoEm
) {}

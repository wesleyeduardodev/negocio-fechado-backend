package com.negociofechado.modulos.solicitacao.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SolicitacaoDetalheResponse(
        Long id,
        String titulo,
        String descricao,
        Long categoriaId,
        String categoriaNome,
        String categoriaIcone,
        String status,
        String urgencia,
        String uf,
        Integer cidadeIbgeId,
        String cidadeNome,
        String bairro,
        List<String> fotos,
        int totalInteresses,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {}

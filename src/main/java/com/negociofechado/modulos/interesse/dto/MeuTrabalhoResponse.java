package com.negociofechado.modulos.interesse.dto;

import java.time.LocalDateTime;

public record MeuTrabalhoResponse(
    Long interesseId,
    Long solicitacaoId,
    String solicitacaoTitulo,
    String solicitacaoDescricao,
    String categoriaNome,
    String categoriaIcone,
    String status,
    String clienteNome,
    String clienteCelular,
    String clienteBairro,
    String clienteCidade,
    String clienteUf,
    LocalDateTime contratadoEm,
    // Avaliacao (se concluido)
    Integer avaliacaoNota,
    String avaliacaoComentario,
    LocalDateTime avaliacaoData
) {}

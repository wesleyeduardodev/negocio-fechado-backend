package com.negociofechado.modulos.solicitacao.dto;

import java.time.LocalDateTime;

public record SolicitacaoParaProfissionalResponse(
    Long id,
    String titulo,
    String descricao,
    String clienteNome,
    String categoriaNome,
    String categoriaIcone,
    String bairro,
    String cidadeNome,
    String uf,
    Integer quantidadeFotos,
    LocalDateTime criadoEm
) {}

package com.negociofechado.modulos.solicitacao.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SolicitacaoParaProfissionalResponse(
    Long id,
    String titulo,
    String descricao,
    String clienteNome,
    String clienteCelular,
    String categoriaNome,
    String categoriaIcone,
    String bairro,
    String cidadeNome,
    String uf,
    String urgencia,
    List<String> fotos,
    LocalDateTime criadoEm
) {}

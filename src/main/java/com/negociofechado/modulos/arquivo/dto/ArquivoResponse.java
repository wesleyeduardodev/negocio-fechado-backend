package com.negociofechado.modulos.arquivo.dto;

public record ArquivoResponse(
    Long id,
    String url,
    String nomeOriginal,
    Long tamanho,
    Integer largura,
    Integer altura,
    Integer ordem
) {}

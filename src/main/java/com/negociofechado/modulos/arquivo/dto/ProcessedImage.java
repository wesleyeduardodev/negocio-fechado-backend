package com.negociofechado.modulos.arquivo.dto;

public record ProcessedImage(
    byte[] bytes,
    String contentType,
    String extensao,
    int largura,
    int altura
) {}

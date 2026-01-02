package com.negociofechado.modulos.autenticacao.dto;

public record UsuarioAuthResponse(
        Long id,
        String nome,
        String celular,
        String fotoUrl,
        String uf,
        Integer cidadeIbgeId,
        String cidadeNome,
        String bairro
) {}

package com.negociofechado.modulos.usuario.dto;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String celular,
        String fotoUrl,
        String uf,
        Integer cidadeIbgeId,
        String cidadeNome,
        String bairro,
        LocalDateTime criadoEm
) {}

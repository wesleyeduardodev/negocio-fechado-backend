package com.negociofechado.modulos.autenticacao.dto;

public record AuthResponse(
        String token,
        String refreshToken,
        UsuarioAuthResponse usuario
) {}

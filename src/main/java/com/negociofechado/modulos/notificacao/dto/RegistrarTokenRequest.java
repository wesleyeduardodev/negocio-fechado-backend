package com.negociofechado.modulos.notificacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.negociofechado.modulos.notificacao.entity.DispositivoToken.Plataforma;

public record RegistrarTokenRequest(
    @NotBlank(message = "Token e obrigatorio")
    String token,

    @NotNull(message = "Plataforma e obrigatoria")
    Plataforma plataforma
) {}

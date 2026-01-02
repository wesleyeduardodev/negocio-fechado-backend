package com.negociofechado.modulos.autenticacao.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Celular é obrigatório")
        String celular,

        @NotBlank(message = "Senha é obrigatória")
        String senha
) {}

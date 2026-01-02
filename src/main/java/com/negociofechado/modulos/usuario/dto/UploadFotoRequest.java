package com.negociofechado.modulos.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record UploadFotoRequest(
        @NotBlank(message = "URL da foto é obrigatória")
        String fotoUrl
) {}

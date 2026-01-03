package com.negociofechado.modulos.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AtualizarModoRequest(
    @NotBlank(message = "Modo e obrigatorio")
    @Pattern(regexp = "^(cliente|profissional)$", message = "Modo deve ser 'cliente' ou 'profissional'")
    String modo
) {}

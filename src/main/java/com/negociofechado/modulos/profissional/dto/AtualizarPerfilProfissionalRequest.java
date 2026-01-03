package com.negociofechado.modulos.profissional.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record AtualizarPerfilProfissionalRequest(
        @NotBlank(message = "Bio é obrigatória")
        @Size(min = 20, message = "Bio deve ter no mínimo 20 caracteres")
        String bio,

        @NotEmpty(message = "Selecione pelo menos uma categoria")
        Set<Long> categoriasIds,

        Boolean ativo
) {}

package com.negociofechado.modulos.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarUsuarioRequest(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "UF é obrigatória")
        @Size(min = 2, max = 2, message = "UF deve ter 2 caracteres")
        String uf,

        @NotNull(message = "Código IBGE da cidade é obrigatório")
        Integer cidadeIbgeId,

        @NotBlank(message = "Nome da cidade é obrigatório")
        String cidadeNome,

        @NotBlank(message = "Bairro é obrigatório")
        String bairro
) {}

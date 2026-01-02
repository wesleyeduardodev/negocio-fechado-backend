package com.negociofechado.modulos.autenticacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrarRequest(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Celular é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "Celular deve ter 11 dígitos")
        String celular,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha,

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

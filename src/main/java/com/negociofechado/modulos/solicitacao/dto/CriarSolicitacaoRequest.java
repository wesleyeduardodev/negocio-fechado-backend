package com.negociofechado.modulos.solicitacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CriarSolicitacaoRequest(
        @NotNull(message = "Categoria é obrigatória")
        Long categoriaId,

        @NotBlank(message = "Título é obrigatório")
        @Size(min = 5, max = 100, message = "Título deve ter entre 5 e 100 caracteres")
        String titulo,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(min = 10, max = 1000, message = "Descrição deve ter entre 10 e 1000 caracteres")
        String descricao,

        List<String> fotos
) {}

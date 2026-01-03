package com.negociofechado.modulos.interesse.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarInteresseRequest(
    @NotNull(message = "ID da solicitacao e obrigatorio")
    Long solicitacaoId,

    @Size(max = 500, message = "Mensagem deve ter no maximo 500 caracteres")
    String mensagem
) {}

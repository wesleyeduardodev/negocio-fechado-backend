package com.negociofechado.modulos.solicitacao.dto;

import com.negociofechado.modulos.solicitacao.entity.Urgencia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AtualizarSolicitacaoRequest(
    @NotBlank(message = "Titulo e obrigatorio")
    @Size(min = 5, max = 100, message = "Titulo deve ter entre 5 e 100 caracteres")
    String titulo,

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(min = 20, max = 2000, message = "Descricao deve ter entre 20 e 2000 caracteres")
    String descricao,

    @NotNull(message = "Urgencia e obrigatoria")
    Urgencia urgencia,

    List<String> fotos
) {}

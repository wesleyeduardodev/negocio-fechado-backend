package com.negociofechado.modulos.orcamento.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record OrcamentoRequest(
    @NotNull(message = "Valor e obrigatorio")
    @DecimalMin(value = "1.00", message = "Valor minimo e R$ 1,00")
    BigDecimal valor,

    @NotBlank(message = "Prazo estimado e obrigatorio")
    @Size(max = 100, message = "Prazo estimado deve ter no maximo 100 caracteres")
    String prazoEstimado,

    @NotBlank(message = "Mensagem e obrigatoria")
    @Size(min = 10, max = 1000, message = "Mensagem deve ter entre 10 e 1000 caracteres")
    String mensagem
) {}

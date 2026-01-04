package com.negociofechado.modulos.avaliacao.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AvaliacaoRequest(
    @NotNull(message = "Nota e obrigatoria")
    @Min(value = 1, message = "Nota minima e 1")
    @Max(value = 5, message = "Nota maxima e 5")
    Integer nota,

    String comentario
) {}

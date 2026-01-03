package com.negociofechado.modulos.avaliacao.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoRequest(
    @NotNull(message = "Nota e obrigatoria")
    @Min(value = 1, message = "Nota minima e 1")
    @Max(value = 5, message = "Nota maxima e 5")
    Integer nota,

    @NotBlank(message = "Comentario e obrigatorio")
    @Size(min = 10, message = "Comentario deve ter no minimo 10 caracteres")
    String comentario
) {}

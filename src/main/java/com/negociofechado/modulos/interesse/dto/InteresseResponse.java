package com.negociofechado.modulos.interesse.dto;
import java.time.LocalDateTime;

public record InteresseResponse(
    Long id,
    Long profissionalId,
    String profissionalNome,
    String profissionalCelular,
    String profissionalBio,
    String profissionalFotoUrl,
    Integer profissionalQuantidadeFotos,
    Double profissionalMediaAvaliacao,
    Long profissionalTotalAvaliacoes,
    String mensagem,
    String status,
    LocalDateTime criadoEm
) {}

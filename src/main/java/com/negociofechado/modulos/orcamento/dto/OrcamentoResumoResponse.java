package com.negociofechado.modulos.orcamento.dto;

import com.negociofechado.modulos.orcamento.entity.Orcamento;
import com.negociofechado.modulos.orcamento.entity.StatusOrcamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrcamentoResumoResponse(
    Long id,
    BigDecimal valor,
    String prazoEstimado,
    String mensagem,
    StatusOrcamento status,
    String profissionalNome,
    Long profissionalId,
    LocalDateTime criadoEm
) {
    public static OrcamentoResumoResponse fromEntity(Orcamento orcamento) {
        return new OrcamentoResumoResponse(
            orcamento.getId(),
            orcamento.getValor(),
            orcamento.getPrazoEstimado(),
            orcamento.getMensagem(),
            orcamento.getStatus(),
            orcamento.getProfissional().getUsuario().getNome(),
            orcamento.getProfissional().getId(),
            orcamento.getCriadoEm()
        );
    }
}

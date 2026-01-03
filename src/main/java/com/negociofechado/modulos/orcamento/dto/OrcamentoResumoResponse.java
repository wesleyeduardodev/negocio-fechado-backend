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
    String profissionalCelular,
    LocalDateTime criadoEm
) {
    public static OrcamentoResumoResponse fromEntity(Orcamento orcamento) {
        String celular = null;
        if (orcamento.getStatus() == StatusOrcamento.ACEITO) {
            celular = orcamento.getProfissional().getUsuario().getCelular();
        }

        return new OrcamentoResumoResponse(
            orcamento.getId(),
            orcamento.getValor(),
            orcamento.getPrazoEstimado(),
            orcamento.getMensagem(),
            orcamento.getStatus(),
            orcamento.getProfissional().getUsuario().getNome(),
            orcamento.getProfissional().getId(),
            celular,
            orcamento.getCriadoEm()
        );
    }
}

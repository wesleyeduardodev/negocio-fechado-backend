package com.negociofechado.modulos.orcamento.dto;

import com.negociofechado.modulos.orcamento.entity.Orcamento;
import com.negociofechado.modulos.orcamento.entity.StatusOrcamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrcamentoEnviadoResponse(
    Long id,
    BigDecimal valor,
    String prazoEstimado,
    String mensagem,
    StatusOrcamento status,
    Long solicitacaoId,
    String solicitacaoTitulo,
    String categoriaNome,
    String categoriaIcone,
    String clienteNome,
    LocalDateTime criadoEm
) {
    public static OrcamentoEnviadoResponse fromEntity(Orcamento orcamento) {
        var solicitacao = orcamento.getSolicitacao();
        var categoria = solicitacao.getCategoria();

        return new OrcamentoEnviadoResponse(
            orcamento.getId(),
            orcamento.getValor(),
            orcamento.getPrazoEstimado(),
            orcamento.getMensagem(),
            orcamento.getStatus(),
            solicitacao.getId(),
            solicitacao.getTitulo(),
            categoria.getNome(),
            categoria.getIcone(),
            solicitacao.getCliente().getNome(),
            orcamento.getCriadoEm()
        );
    }
}

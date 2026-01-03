package com.negociofechado.modulos.orcamento.document;

import com.negociofechado.modulos.orcamento.dto.OrcamentoEnviadoResponse;
import com.negociofechado.modulos.orcamento.dto.OrcamentoRequest;
import com.negociofechado.modulos.orcamento.dto.OrcamentoResumoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Orcamentos", description = "Endpoints para gerenciamento de orcamentos")
public interface OrcamentoDocument {

    @Operation(summary = "Enviar orcamento", description = "Profissional envia um orcamento para uma solicitacao")
    ResponseEntity<OrcamentoResumoResponse> enviar(Long usuarioId, Long solicitacaoId, OrcamentoRequest request);

    @Operation(summary = "Listar orcamentos da solicitacao", description = "Cliente lista os orcamentos recebidos para uma solicitacao")
    ResponseEntity<List<OrcamentoResumoResponse>> listarPorSolicitacao(Long usuarioId, Long solicitacaoId);

    @Operation(summary = "Listar orcamentos enviados", description = "Profissional lista seus orcamentos enviados")
    ResponseEntity<Page<OrcamentoEnviadoResponse>> listarEnviados(Long usuarioId, Pageable pageable);

    @Operation(summary = "Aceitar orcamento", description = "Cliente aceita um orcamento")
    ResponseEntity<Void> aceitar(Long usuarioId, Long orcamentoId);

    @Operation(summary = "Recusar orcamento", description = "Cliente recusa um orcamento")
    ResponseEntity<Void> recusar(Long usuarioId, Long orcamentoId);
}

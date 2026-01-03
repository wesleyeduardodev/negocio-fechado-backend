package com.negociofechado.modulos.avaliacao.document;

import com.negociofechado.modulos.avaliacao.dto.AvaliacaoRequest;
import com.negociofechado.modulos.avaliacao.dto.AvaliacaoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Avaliacoes", description = "Endpoints de avaliacoes de servicos")
public interface AvaliacaoDocument {

    @Operation(summary = "Criar avaliacao", description = "Avalia um servico concluido")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Avaliacao criada"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos ou solicitacao nao pode ser avaliada"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "404", description = "Solicitacao nao encontrada")
    })
    ResponseEntity<AvaliacaoResponse> criar(
            @Parameter(hidden = true) Long usuarioId,
            Long solicitacaoId,
            AvaliacaoRequest request);

    @Operation(summary = "Listar avaliacoes do profissional", description = "Lista todas as avaliacoes recebidas por um profissional")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de avaliacoes"),
            @ApiResponse(responseCode = "404", description = "Profissional nao encontrado")
    })
    ResponseEntity<Page<AvaliacaoResponse>> listarPorProfissional(
            Long profissionalId,
            Pageable pageable);

}

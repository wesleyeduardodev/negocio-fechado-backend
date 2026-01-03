package com.negociofechado.modulos.solicitacao.document;

import com.negociofechado.modulos.solicitacao.dto.CriarSolicitacaoRequest;
import com.negociofechado.modulos.solicitacao.dto.SolicitacaoDetalheResponse;
import com.negociofechado.modulos.solicitacao.dto.SolicitacaoParaProfissionalResponse;
import com.negociofechado.modulos.solicitacao.dto.SolicitacaoResumoResponse;
import com.negociofechado.modulos.solicitacao.dto.SolicitacoesStatsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Solicitações", description = "Endpoints de solicitações de serviço")
public interface SolicitacaoDocument {

    @Operation(summary = "Criar solicitação", description = "Cria uma nova solicitação de serviço")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitação criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<SolicitacaoDetalheResponse> criar(
            @Parameter(hidden = true) Long usuarioId,
            CriarSolicitacaoRequest request);

    @Operation(summary = "Listar minhas solicitações", description = "Lista as solicitações do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de solicitações"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<Page<SolicitacaoResumoResponse>> listar(
            @Parameter(hidden = true) Long usuarioId,
            Pageable pageable);

    @Operation(summary = "Buscar solicitação", description = "Busca uma solicitação pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalhes da solicitação"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada")
    })
    ResponseEntity<SolicitacaoDetalheResponse> buscarPorId(
            @Parameter(hidden = true) Long usuarioId,
            Long id);

    @Operation(summary = "Cancelar solicitação", description = "Cancela uma solicitação aberta")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Solicitação cancelada"),
            @ApiResponse(responseCode = "400", description = "Solicitação não pode ser cancelada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada")
    })
    ResponseEntity<Void> cancelar(
            @Parameter(hidden = true) Long usuarioId,
            Long id);

    @Operation(summary = "Estatísticas", description = "Retorna estatísticas das solicitações do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estatísticas"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<SolicitacoesStatsResponse> stats(
            @Parameter(hidden = true) Long usuarioId);

    @Operation(summary = "Listar solicitações disponíveis", description = "Lista solicitações disponíveis para o profissional na sua região e categorias")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de solicitações disponíveis"),
            @ApiResponse(responseCode = "400", description = "Profissional não cadastrado ou inativo"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<Page<SolicitacaoParaProfissionalResponse>> listarDisponiveis(
            @Parameter(hidden = true) Long usuarioId,
            Pageable pageable);

    @Operation(summary = "Buscar solicitação disponível", description = "Busca detalhes de uma solicitação disponível para o profissional")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalhes da solicitação"),
            @ApiResponse(responseCode = "400", description = "Solicitação não disponível para você"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada")
    })
    ResponseEntity<SolicitacaoDetalheResponse> buscarDisponivelPorId(
            @Parameter(hidden = true) Long usuarioId,
            Long id);

}

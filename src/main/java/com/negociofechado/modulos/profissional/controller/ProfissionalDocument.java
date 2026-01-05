package com.negociofechado.modulos.profissional.controller;
import com.negociofechado.modulos.profissional.dto.AtualizarPerfilProfissionalRequest;
import com.negociofechado.modulos.profissional.dto.CriarPerfilProfissionalRequest;
import com.negociofechado.modulos.profissional.dto.PerfilProfissionalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;

@Tag(name = "Profissionais", description = "Gerenciamento de perfis profissionais")
public interface ProfissionalDocument {

    @Operation(summary = "Criar perfil profissional", description = "Transforma o usuário em um profissional")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou usuário já é profissional"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<PerfilProfissionalResponse> criar(Long usuarioId, CriarPerfilProfissionalRequest request);

    @Operation(summary = "Buscar meu perfil profissional", description = "Retorna o perfil profissional do usuário logado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
            @ApiResponse(responseCode = "400", description = "Usuário não possui perfil profissional"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<PerfilProfissionalResponse> buscarMeuPerfil(Long usuarioId);

    @Operation(summary = "Atualizar meu perfil profissional", description = "Atualiza bio e categorias do perfil")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou usuário não possui perfil"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<PerfilProfissionalResponse> atualizar(Long usuarioId, AtualizarPerfilProfissionalRequest request);

    @Operation(summary = "Buscar perfil profissional por ID", description = "Retorna o perfil público de um profissional")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    ResponseEntity<PerfilProfissionalResponse> buscarPorId(Long id);

    @Operation(summary = "Verificar se usuário é profissional", description = "Retorna se o usuário logado possui perfil profissional")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status retornado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<Boolean> isProfissional(Long usuarioId);
}

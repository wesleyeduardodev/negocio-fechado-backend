package com.negociofechado.modulos.usuario.controller;

import com.negociofechado.modulos.usuario.dto.AlterarSenhaRequest;
import com.negociofechado.modulos.usuario.dto.AtualizarUsuarioRequest;
import com.negociofechado.modulos.usuario.dto.UsuarioResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Usuários", description = "Endpoints de gerenciamento do usuário logado")
public interface UsuarioDocument {

    @Operation(summary = "Buscar meus dados", description = "Retorna os dados do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados do usuário"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<UsuarioResponse> buscarMeusDados(
            @Parameter(hidden = true) Long usuarioId);

    @Operation(summary = "Atualizar meus dados", description = "Atualiza nome e endereço do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados atualizados"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<UsuarioResponse> atualizar(
            @Parameter(hidden = true) Long usuarioId,
            AtualizarUsuarioRequest request);

    @Operation(summary = "Alterar senha", description = "Altera a senha do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha inválida"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<Void> alterarSenha(
            @Parameter(hidden = true) Long usuarioId,
            AlterarSenhaRequest request);

    @Operation(summary = "Atualizar foto", description = "Atualiza a foto de perfil do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Foto atualizada"),
            @ApiResponse(responseCode = "400", description = "Arquivo invalido"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<UsuarioResponse> atualizarFoto(
            @Parameter(hidden = true) Long usuarioId,
            @Parameter(description = "Foto de perfil (JPG/PNG/WebP, max 10MB)")
            MultipartFile foto);

    @Operation(summary = "Remover foto", description = "Remove a foto de perfil do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Foto removida"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<Void> removerFoto(
            @Parameter(hidden = true) Long usuarioId);

}

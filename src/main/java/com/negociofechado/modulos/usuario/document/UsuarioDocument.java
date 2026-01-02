package com.negociofechado.modulos.usuario.document;

import com.negociofechado.modulos.usuario.dto.AlterarSenhaRequest;
import com.negociofechado.modulos.usuario.dto.AtualizarUsuarioRequest;
import com.negociofechado.modulos.usuario.dto.UploadFotoRequest;
import com.negociofechado.modulos.usuario.dto.UsuarioResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;

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
            @ApiResponse(responseCode = "400", description = "URL inválida"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<UsuarioResponse> atualizarFoto(
            @Parameter(hidden = true) Long usuarioId,
            UploadFotoRequest request);

}

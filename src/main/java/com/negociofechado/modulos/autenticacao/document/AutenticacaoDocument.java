package com.negociofechado.modulos.autenticacao.document;

import com.negociofechado.modulos.autenticacao.dto.AuthResponse;
import com.negociofechado.modulos.autenticacao.dto.LoginRequest;
import com.negociofechado.modulos.autenticacao.dto.RefreshRequest;
import com.negociofechado.modulos.autenticacao.dto.RegistrarRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Autenticação", description = "Endpoints de autenticação")
public interface AutenticacaoDocument {

    @Operation(
            summary = "Registrar novo usuário",
            description = "Cria uma nova conta de usuário",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "409", description = "Celular já cadastrado")
            }
    )
    AuthResponse registrar(RegistrarRequest request);

    @Operation(
            summary = "Login",
            description = "Autentica um usuário e retorna tokens de acesso",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
            }
    )
    AuthResponse login(LoginRequest request);

    @Operation(
            summary = "Refresh Token",
            description = "Gera novos tokens a partir do refresh token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tokens renovados com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado")
            }
    )
    AuthResponse refresh(RefreshRequest request);

}

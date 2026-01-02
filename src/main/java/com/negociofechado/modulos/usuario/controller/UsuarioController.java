package com.negociofechado.modulos.usuario.controller;

import com.negociofechado.modulos.usuario.document.UsuarioDocument;
import com.negociofechado.modulos.usuario.dto.AlterarSenhaRequest;
import com.negociofechado.modulos.usuario.dto.AtualizarUsuarioRequest;
import com.negociofechado.modulos.usuario.dto.UploadFotoRequest;
import com.negociofechado.modulos.usuario.dto.UsuarioResponse;
import com.negociofechado.modulos.usuario.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController implements UsuarioDocument {

    private final UsuarioService usuarioService;

    @Override
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> buscarMeusDados(
            @AuthenticationPrincipal Long usuarioId) {
        UsuarioResponse response = usuarioService.buscarPorId(usuarioId);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizar(
            @AuthenticationPrincipal Long usuarioId,
            @Valid @RequestBody AtualizarUsuarioRequest request) {
        UsuarioResponse response = usuarioService.atualizar(usuarioId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("/me/senha")
    public ResponseEntity<Void> alterarSenha(
            @AuthenticationPrincipal Long usuarioId,
            @Valid @RequestBody AlterarSenhaRequest request) {
        usuarioService.alterarSenha(usuarioId, request);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/me/foto")
    public ResponseEntity<UsuarioResponse> atualizarFoto(
            @AuthenticationPrincipal Long usuarioId,
            @Valid @RequestBody UploadFotoRequest request) {
        UsuarioResponse response = usuarioService.atualizarFoto(usuarioId, request);
        return ResponseEntity.ok(response);
    }

}

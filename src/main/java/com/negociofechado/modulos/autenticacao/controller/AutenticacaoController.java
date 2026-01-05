package com.negociofechado.modulos.autenticacao.controller;

import com.negociofechado.modulos.autenticacao.dto.AuthResponse;
import com.negociofechado.modulos.autenticacao.dto.LoginRequest;
import com.negociofechado.modulos.autenticacao.dto.RefreshRequest;
import com.negociofechado.modulos.autenticacao.dto.RegistrarRequest;
import com.negociofechado.modulos.autenticacao.service.AutenticacaoService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AutenticacaoController implements AutenticacaoDocument {

    private final AutenticacaoService autenticacaoService;

    @Override
    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registrar(@RequestBody @Valid RegistrarRequest request) {
        return autenticacaoService.registrar(request);
    }

    @Override
    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        return autenticacaoService.login(request);
    }

    @Override
    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody @Valid RefreshRequest request) {
        return autenticacaoService.refresh(request);
    }

}

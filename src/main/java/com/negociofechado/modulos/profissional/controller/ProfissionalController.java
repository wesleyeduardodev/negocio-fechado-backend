package com.negociofechado.modulos.profissional.controller;
import com.negociofechado.modulos.profissional.dto.AtualizarPerfilProfissionalRequest;
import com.negociofechado.modulos.profissional.dto.CriarPerfilProfissionalRequest;
import com.negociofechado.modulos.profissional.dto.PerfilProfissionalResponse;
import com.negociofechado.modulos.profissional.service.ProfissionalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/profissionais")
@RequiredArgsConstructor
public class ProfissionalController implements ProfissionalDocument {

    private final ProfissionalService profissionalService;

    @Override
    @PostMapping
    public ResponseEntity<PerfilProfissionalResponse> criar(
            @AuthenticationPrincipal Long usuarioId,
            @Valid @RequestBody CriarPerfilProfissionalRequest request) {
        PerfilProfissionalResponse response = profissionalService.criar(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<PerfilProfissionalResponse> buscarMeuPerfil(
            @AuthenticationPrincipal Long usuarioId) {
        PerfilProfissionalResponse response = profissionalService.buscarMeuPerfil(usuarioId);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("/me")
    public ResponseEntity<PerfilProfissionalResponse> atualizar(
            @AuthenticationPrincipal Long usuarioId,
            @Valid @RequestBody AtualizarPerfilProfissionalRequest request) {
        PerfilProfissionalResponse response = profissionalService.atualizar(usuarioId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PerfilProfissionalResponse> buscarPorId(@PathVariable Long id) {
        PerfilProfissionalResponse response = profissionalService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/me/status")
    public ResponseEntity<Boolean> isProfissional(@AuthenticationPrincipal Long usuarioId) {
        boolean isProfissional = profissionalService.isProfissional(usuarioId);
        return ResponseEntity.ok(isProfissional);
    }
}

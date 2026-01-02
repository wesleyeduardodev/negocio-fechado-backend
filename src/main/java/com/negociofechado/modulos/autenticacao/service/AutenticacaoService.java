package com.negociofechado.modulos.autenticacao.service;

import com.negociofechado.comum.entity.Endereco;
import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.comum.exception.RecursoNaoEncontradoException;
import com.negociofechado.comum.security.JwtService;
import com.negociofechado.modulos.autenticacao.dto.AuthResponse;
import com.negociofechado.modulos.autenticacao.dto.LoginRequest;
import com.negociofechado.modulos.autenticacao.dto.RefreshRequest;
import com.negociofechado.modulos.autenticacao.dto.RegistrarRequest;
import com.negociofechado.modulos.autenticacao.dto.UsuarioAuthResponse;
import com.negociofechado.modulos.localizacao.service.LocalizacaoService;
import com.negociofechado.modulos.usuario.entity.Usuario;
import com.negociofechado.modulos.usuario.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final LocalizacaoService localizacaoService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse registrar(RegistrarRequest request) {
        validarCelularDisponivel(request.celular());

        Endereco endereco = localizacaoService.criarEndereco(
                request.uf(),
                request.cidadeIbgeId(),
                request.cidadeNome(),
                request.bairro()
        );

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .celular(request.celular())
                .senha(passwordEncoder.encode(request.senha()))
                .endereco(endereco)
                .build();

        usuarioRepository.save(usuario);

        return gerarAuthResponse(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCelular(request.celular())
                .orElseThrow(() -> new NegocioException("Celular ou senha inválidos"));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new NegocioException("Celular ou senha inválidos");
        }

        if (!usuario.getAtivo()) {
            throw new NegocioException("Usuário inativo");
        }

        return gerarAuthResponse(usuario);
    }

    public AuthResponse refresh(RefreshRequest request) {
        if (!jwtService.isTokenValido(request.refreshToken())) {
            throw new NegocioException("Refresh token inválido ou expirado");
        }

        Long usuarioId = jwtService.getUsuarioIdDoToken(request.refreshToken());
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", usuarioId));

        return gerarAuthResponse(usuario);
    }

    private void validarCelularDisponivel(String celular) {
        if (usuarioRepository.existsByCelular(celular)) {
            throw new NegocioException("Celular já cadastrado");
        }
    }

    private AuthResponse gerarAuthResponse(Usuario usuario) {
        String token = jwtService.gerarToken(usuario.getId());
        String refreshToken = jwtService.gerarRefreshToken(usuario.getId());

        Endereco endereco = usuario.getEndereco();

        UsuarioAuthResponse usuarioResponse = new UsuarioAuthResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCelular(),
                usuario.getFotoUrl(),
                endereco.getUf(),
                endereco.getCidadeIbgeId(),
                endereco.getCidadeNome(),
                endereco.getBairro()
        );

        return new AuthResponse(token, refreshToken, usuarioResponse);
    }

}

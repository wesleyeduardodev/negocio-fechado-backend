package com.negociofechado.modulos.notificacao.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.modulos.notificacao.dto.NotificacaoResponse;
import com.negociofechado.modulos.notificacao.dto.RegistrarTokenRequest;
import com.negociofechado.modulos.notificacao.entity.DispositivoToken;
import com.negociofechado.modulos.notificacao.entity.Notificacao;
import com.negociofechado.modulos.notificacao.entity.TipoNotificacao;
import com.negociofechado.modulos.notificacao.repository.DispositivoTokenRepository;
import com.negociofechado.modulos.notificacao.repository.NotificacaoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final DispositivoTokenRepository dispositivoTokenRepository;
    private final ExpoPushService expoPushService;

    @Transactional
    public void registrarToken(Long usuarioId, RegistrarTokenRequest request) {
        dispositivoTokenRepository.findByToken(request.token())
            .ifPresentOrElse(
                token -> {
                    token.setUsuarioId(usuarioId);
                    token.setPlataforma(request.plataforma());
                    token.setAtivo(true);
                    dispositivoTokenRepository.save(token);
                },
                () -> {
                    DispositivoToken novoToken = DispositivoToken.builder()
                        .usuarioId(usuarioId)
                        .token(request.token())
                        .plataforma(request.plataforma())
                        .ativo(true)
                        .build();
                    dispositivoTokenRepository.save(novoToken);
                }
            );
    }

    @Transactional
    public void removerToken(String token) {
        dispositivoTokenRepository.findByToken(token).ifPresent(t -> {
            t.setAtivo(false);
            dispositivoTokenRepository.save(t);
        });
    }

    @Transactional
    public void enviarParaUsuario(Long usuarioId, TipoNotificacao tipo, String titulo, String corpo, Long referenciaId) {
        Notificacao notificacao = Notificacao.builder()
            .usuarioId(usuarioId)
            .tipo(tipo)
            .titulo(titulo)
            .corpo(corpo)
            .referenciaId(referenciaId)
            .lida(false)
            .build();

        notificacaoRepository.save(notificacao);

        expoPushService.enviarParaUsuario(usuarioId, titulo, corpo, tipo, referenciaId);
    }

    @Transactional
    public void enviarParaUsuarios(List<Long> usuarioIds, TipoNotificacao tipo, String titulo, String corpo, Long referenciaId) {
        for (Long usuarioId : usuarioIds) {
            Notificacao notificacao = Notificacao.builder()
                .usuarioId(usuarioId)
                .tipo(tipo)
                .titulo(titulo)
                .corpo(corpo)
                .referenciaId(referenciaId)
                .lida(false)
                .build();

            notificacaoRepository.save(notificacao);
        }

        expoPushService.enviarParaUsuarios(usuarioIds, titulo, corpo, tipo, referenciaId);
    }

    public List<NotificacaoResponse> listar(Long usuarioId) {
        return notificacaoRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public long contarNaoLidas(Long usuarioId) {
        return notificacaoRepository.countByUsuarioIdAndLidaFalse(usuarioId);
    }

    @Transactional
    public void marcarComoLida(Long notificacaoId, Long usuarioId) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
            .orElseThrow(() -> new NegocioException("Notificacao nao encontrada"));

        if (!notificacao.getUsuarioId().equals(usuarioId)) {
            throw new NegocioException("Notificacao nao pertence ao usuario");
        }

        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }

    @Transactional
    public void marcarTodasComoLidas(Long usuarioId) {
        notificacaoRepository.marcarTodasComoLidas(usuarioId);
    }

    private NotificacaoResponse toResponse(Notificacao notificacao) {
        return new NotificacaoResponse(
            notificacao.getId(),
            notificacao.getTipo(),
            notificacao.getTitulo(),
            notificacao.getCorpo(),
            notificacao.getReferenciaId(),
            notificacao.getLida(),
            notificacao.getCriadoEm()
        );
    }
}

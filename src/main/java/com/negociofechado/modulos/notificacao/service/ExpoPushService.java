package com.negociofechado.modulos.notificacao.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.negociofechado.modulos.notificacao.entity.DispositivoToken;
import com.negociofechado.modulos.notificacao.entity.TipoNotificacao;
import com.negociofechado.modulos.notificacao.repository.DispositivoTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpoPushService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final DispositivoTokenRepository dispositivoTokenRepository;
    private final RestTemplate restTemplate;

    public void enviarParaUsuario(Long usuarioId, String titulo, String corpo, TipoNotificacao tipo, Long referenciaId) {
        List<DispositivoToken> tokens = dispositivoTokenRepository.findByUsuarioIdAndAtivoTrue(usuarioId);

        if (tokens.isEmpty()) {
            log.debug("Nenhum token encontrado para usuario {}", usuarioId);
            return;
        }

        enviarPush(tokens, titulo, corpo, tipo, referenciaId);
    }

    public void enviarParaUsuarios(List<Long> usuarioIds, String titulo, String corpo, TipoNotificacao tipo, Long referenciaId) {
        List<DispositivoToken> tokens = dispositivoTokenRepository.findByUsuarioIdInAndAtivoTrue(usuarioIds);

        if (tokens.isEmpty()) {
            log.debug("Nenhum token encontrado para usuarios {}", usuarioIds);
            return;
        }

        enviarPush(tokens, titulo, corpo, tipo, referenciaId);
    }

    private void enviarPush(List<DispositivoToken> tokens, String titulo, String corpo, TipoNotificacao tipo, Long referenciaId) {
        List<Map<String, Object>> messages = new ArrayList<>();

        for (DispositivoToken token : tokens) {
            Map<String, Object> message = new HashMap<>();
            message.put("to", token.getToken());
            message.put("title", titulo);
            message.put("body", corpo);
            message.put("sound", "default");

            Map<String, Object> data = new HashMap<>();
            data.put("tipo", tipo.name());
            if (referenciaId != null) {
                data.put("referenciaId", referenciaId);
            }
            message.put("data", data);

            messages.add(message);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            headers.set("Accept-Encoding", "gzip, deflate");

            HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(messages, headers);

            restTemplate.postForEntity(EXPO_PUSH_URL, request, String.class);

            log.info("Push enviado para {} dispositivos", tokens.size());
        } catch (Exception e) {
            log.error("Erro ao enviar push notification: {}", e.getMessage());
        }
    }

    public void desativarToken(String token) {
        dispositivoTokenRepository.findByToken(token).ifPresent(t -> {
            t.setAtivo(false);
            dispositivoTokenRepository.save(t);
            log.info("Token desativado: {}", token);
        });
    }
}

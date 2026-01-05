package com.negociofechado.modulos.notificacao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.negociofechado.modulos.notificacao.entity.DispositivoToken;

public interface DispositivoTokenRepository extends JpaRepository<DispositivoToken, Long> {

    List<DispositivoToken> findByUsuarioIdAndAtivoTrue(Long usuarioId);

    List<DispositivoToken> findByUsuarioIdInAndAtivoTrue(List<Long> usuarioIds);

    Optional<DispositivoToken> findByToken(String token);

    void deleteByUsuarioId(Long usuarioId);
}

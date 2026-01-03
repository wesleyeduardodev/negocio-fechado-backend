package com.negociofechado.modulos.profissional.repository;

import com.negociofechado.modulos.profissional.entity.PerfilProfissional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PerfilProfissionalRepository extends JpaRepository<PerfilProfissional, Long> {

    @Query("SELECT p FROM PerfilProfissional p JOIN FETCH p.usuario WHERE p.usuario.id = :usuarioId")
    Optional<PerfilProfissional> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT p FROM PerfilProfissional p JOIN FETCH p.usuario WHERE p.id = :id AND p.ativo = true")
    Optional<PerfilProfissional> findByIdAndAtivoTrue(@Param("id") Long id);

    boolean existsByUsuarioId(Long usuarioId);

}

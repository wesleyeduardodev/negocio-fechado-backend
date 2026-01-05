package com.negociofechado.modulos.profissional.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.negociofechado.modulos.profissional.entity.PerfilProfissional;

public interface PerfilProfissionalRepository extends JpaRepository<PerfilProfissional, Long> {

    @Query("SELECT p FROM PerfilProfissional p JOIN FETCH p.usuario WHERE p.usuario.id = :usuarioId")
    Optional<PerfilProfissional> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT p FROM PerfilProfissional p JOIN FETCH p.usuario WHERE p.id = :id AND p.ativo = true")
    Optional<PerfilProfissional> findByIdAndAtivoTrue(@Param("id") Long id);

    boolean existsByUsuarioId(Long usuarioId);

    @Query("""
        SELECT DISTINCT p.usuario.id FROM PerfilProfissional p
        JOIN p.categorias c
        WHERE p.usuario.endereco.cidadeIbgeId = :cidadeIbgeId
        AND c.id = :categoriaId
        AND p.ativo = true
        AND p.usuario.id != :excluirUsuarioId
    """)
    List<Long> findUsuarioIdsByCidadeAndCategoriaAndAtivoExcluindoUsuario(
        @Param("cidadeIbgeId") Integer cidadeIbgeId,
        @Param("categoriaId") Long categoriaId,
        @Param("excluirUsuarioId") Long excluirUsuarioId
    );
}

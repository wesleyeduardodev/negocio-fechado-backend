package com.negociofechado.modulos.solicitacao.repository;

import com.negociofechado.modulos.solicitacao.entity.Solicitacao;
import com.negociofechado.modulos.solicitacao.entity.StatusSolicitacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    @Query(value = "SELECT s FROM Solicitacao s JOIN FETCH s.categoria WHERE s.cliente.id = :clienteId ORDER BY s.criadoEm DESC",
           countQuery = "SELECT COUNT(s) FROM Solicitacao s WHERE s.cliente.id = :clienteId")
    Page<Solicitacao> findByClienteIdOrderByCriadoEmDesc(@Param("clienteId") Long clienteId, Pageable pageable);

    @Query("SELECT s FROM Solicitacao s JOIN FETCH s.categoria WHERE s.cliente.id = :clienteId AND s.status = :status ORDER BY s.criadoEm DESC")
    List<Solicitacao> findByClienteIdAndStatusOrderByCriadoEmDesc(@Param("clienteId") Long clienteId, @Param("status") StatusSolicitacao status);

    @Query("SELECT s FROM Solicitacao s JOIN FETCH s.categoria WHERE s.id = :id AND s.cliente.id = :clienteId")
    Optional<Solicitacao> findByIdAndClienteId(@Param("id") Long id, @Param("clienteId") Long clienteId);

    @Query("SELECT s.status, COUNT(s) FROM Solicitacao s WHERE s.cliente.id = :clienteId GROUP BY s.status")
    List<Object[]> countByClienteIdGroupByStatus(@Param("clienteId") Long clienteId);

    @Query(value = """
        SELECT s FROM Solicitacao s
        JOIN FETCH s.categoria c
        JOIN FETCH s.cliente cl
        WHERE s.status = 'ABERTA'
        AND s.endereco.cidadeIbgeId = :cidadeIbgeId
        AND c.id IN :categoriasIds
        AND cl.id != :profissionalUsuarioId
        ORDER BY s.criadoEm DESC
        """,
        countQuery = """
        SELECT COUNT(s) FROM Solicitacao s
        WHERE s.status = 'ABERTA'
        AND s.endereco.cidadeIbgeId = :cidadeIbgeId
        AND s.categoria.id IN :categoriasIds
        AND s.cliente.id != :profissionalUsuarioId
        """)
    Page<Solicitacao> findDisponiveisParaProfissional(
        @Param("cidadeIbgeId") Integer cidadeIbgeId,
        @Param("categoriasIds") Set<Long> categoriasIds,
        @Param("profissionalUsuarioId") Long profissionalUsuarioId,
        Pageable pageable
    );

    @Query("SELECT s FROM Solicitacao s JOIN FETCH s.categoria JOIN FETCH s.cliente WHERE s.id = :id AND s.status = 'ABERTA'")
    Optional<Solicitacao> findByIdAndStatusAberta(@Param("id") Long id);

}

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

}

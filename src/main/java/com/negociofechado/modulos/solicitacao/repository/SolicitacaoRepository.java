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

    Page<Solicitacao> findByClienteIdOrderByCriadoEmDesc(Long clienteId, Pageable pageable);

    List<Solicitacao> findByClienteIdAndStatusOrderByCriadoEmDesc(Long clienteId, StatusSolicitacao status);

    @Query("SELECT s FROM Solicitacao s WHERE s.id = :id AND s.cliente.id = :clienteId")
    Optional<Solicitacao> findByIdAndClienteId(@Param("id") Long id, @Param("clienteId") Long clienteId);

    @Query("SELECT COUNT(s) FROM Solicitacao s WHERE s.cliente.id = :clienteId")
    long countByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT COUNT(s) FROM Solicitacao s WHERE s.cliente.id = :clienteId AND s.status = :status")
    long countByClienteIdAndStatus(@Param("clienteId") Long clienteId, @Param("status") StatusSolicitacao status);

}

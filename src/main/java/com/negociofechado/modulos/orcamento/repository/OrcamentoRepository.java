package com.negociofechado.modulos.orcamento.repository;

import com.negociofechado.modulos.orcamento.entity.Orcamento;
import com.negociofechado.modulos.orcamento.entity.StatusOrcamento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    @Query("""
        SELECT o FROM Orcamento o
        JOIN FETCH o.profissional p
        JOIN FETCH p.usuario u
        WHERE o.solicitacao.id = :solicitacaoId
        ORDER BY o.criadoEm DESC
        """)
    List<Orcamento> findBySolicitacaoIdWithProfissional(@Param("solicitacaoId") Long solicitacaoId);

    @Query("""
        SELECT o FROM Orcamento o
        JOIN FETCH o.solicitacao s
        JOIN FETCH s.categoria c
        JOIN FETCH s.cliente cl
        WHERE o.profissional.id = :profissionalId
        ORDER BY o.criadoEm DESC
        """)
    Page<Orcamento> findByProfissionalIdWithSolicitacao(
        @Param("profissionalId") Long profissionalId,
        Pageable pageable
    );

    @Query("""
        SELECT o FROM Orcamento o
        JOIN FETCH o.solicitacao s
        JOIN FETCH s.categoria c
        JOIN FETCH o.profissional p
        JOIN FETCH p.usuario u
        WHERE o.id = :id
        """)
    Optional<Orcamento> findByIdWithDetails(@Param("id") Long id);

    boolean existsBySolicitacaoIdAndProfissionalId(Long solicitacaoId, Long profissionalId);

    @Query("SELECT COUNT(o) FROM Orcamento o WHERE o.solicitacao.id = :solicitacaoId")
    Integer countBySolicitacaoId(@Param("solicitacaoId") Long solicitacaoId);

    @Modifying
    @Query("""
        UPDATE Orcamento o
        SET o.status = :status, o.atualizadoEm = CURRENT_TIMESTAMP
        WHERE o.solicitacao.id = :solicitacaoId
        AND o.id != :orcamentoAceitoId
        AND o.status = 'PENDENTE'
        """)
    void recusarOutrosOrcamentos(
        @Param("solicitacaoId") Long solicitacaoId,
        @Param("orcamentoAceitoId") Long orcamentoAceitoId,
        @Param("status") StatusOrcamento status
    );

    @Query("""
        SELECT o FROM Orcamento o
        JOIN FETCH o.profissional p
        WHERE o.solicitacao.id = :solicitacaoId
        AND o.status = 'ACEITO'
        """)
    Optional<Orcamento> findAceitoBySolicitacaoId(@Param("solicitacaoId") Long solicitacaoId);
}

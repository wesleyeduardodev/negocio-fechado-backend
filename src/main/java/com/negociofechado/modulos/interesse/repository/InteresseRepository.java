package com.negociofechado.modulos.interesse.repository;

import com.negociofechado.modulos.interesse.entity.Interesse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.negociofechado.modulos.interesse.entity.StatusInteresse;

import java.util.List;
import java.util.Optional;

@Repository
public interface InteresseRepository extends JpaRepository<Interesse, Long> {

    Optional<Interesse> findBySolicitacaoIdAndProfissionalId(Long solicitacaoId, Long profissionalId);

    @Query("SELECT i FROM Interesse i WHERE i.solicitacao.id = :solicitacaoId AND i.status = 'CONTRATADO'")
    Optional<Interesse> findContratadoBySolicitacaoId(@Param("solicitacaoId") Long solicitacaoId);

    boolean existsBySolicitacaoIdAndProfissionalId(Long solicitacaoId, Long profissionalId);

    @Query("SELECT i FROM Interesse i " +
           "JOIN FETCH i.profissional p " +
           "JOIN FETCH p.usuario u " +
           "WHERE i.solicitacao.id = :solicitacaoId " +
           "ORDER BY i.criadoEm DESC")
    List<Interesse> findBySolicitacaoIdWithProfissional(@Param("solicitacaoId") Long solicitacaoId);

    @Query("SELECT i FROM Interesse i " +
           "JOIN FETCH i.solicitacao s " +
           "JOIN FETCH s.categoria c " +
           "WHERE i.profissional.id = :profissionalId " +
           "ORDER BY i.criadoEm DESC")
    Page<Interesse> findByProfissionalIdWithSolicitacao(@Param("profissionalId") Long profissionalId, Pageable pageable);

    int countBySolicitacaoId(Long solicitacaoId);

    @Query("SELECT COUNT(i) FROM Interesse i WHERE i.profissional.id = :profissionalId")
    int countByProfissionalId(@Param("profissionalId") Long profissionalId);

    @Query("SELECT COUNT(i) FROM Interesse i WHERE i.profissional.id = :profissionalId AND i.status = 'CONTRATADO'")
    int countContratadosByProfissionalId(@Param("profissionalId") Long profissionalId);

    @Query("SELECT COUNT(i) FROM Interesse i WHERE i.profissional.id = :profissionalId AND i.status IN ('PENDENTE', 'VISUALIZADO')")
    int countEmNegociacaoByProfissionalId(@Param("profissionalId") Long profissionalId);

    @Query("SELECT i FROM Interesse i " +
           "JOIN FETCH i.solicitacao s " +
           "JOIN FETCH s.categoria c " +
           "JOIN FETCH s.cliente cl " +
           "JOIN FETCH s.endereco e " +
           "WHERE i.profissional.id = :profissionalId " +
           "AND i.status = 'CONTRATADO' " +
           "ORDER BY i.atualizadoEm DESC")
    List<Interesse> findTrabalhosByProfissionalId(@Param("profissionalId") Long profissionalId);
}

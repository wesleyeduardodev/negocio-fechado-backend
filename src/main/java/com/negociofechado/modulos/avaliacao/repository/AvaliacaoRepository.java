package com.negociofechado.modulos.avaliacao.repository;
import com.negociofechado.modulos.avaliacao.entity.Avaliacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    Optional<Avaliacao> findBySolicitacaoId(Long solicitacaoId);

    boolean existsBySolicitacaoId(Long solicitacaoId);

    Page<Avaliacao> findByProfissionalIdOrderByCriadoEmDesc(Long profissionalId, Pageable pageable);

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.profissional.id = :profissionalId")
    Double calcularMediaPorProfissional(@Param("profissionalId") Long profissionalId);

    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.profissional.id = :profissionalId")
    Long contarPorProfissional(@Param("profissionalId") Long profissionalId);
}

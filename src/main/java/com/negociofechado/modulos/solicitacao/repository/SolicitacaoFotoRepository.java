package com.negociofechado.modulos.solicitacao.repository;

import com.negociofechado.modulos.solicitacao.entity.SolicitacaoFoto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoFotoRepository extends JpaRepository<SolicitacaoFoto, Long> {

    List<SolicitacaoFoto> findBySolicitacaoIdOrderByOrdem(Long solicitacaoId);

    int countBySolicitacaoId(Long solicitacaoId);

    void deleteBySolicitacaoId(Long solicitacaoId);
}

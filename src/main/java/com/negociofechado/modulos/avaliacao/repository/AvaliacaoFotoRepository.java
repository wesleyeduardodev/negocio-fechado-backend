package com.negociofechado.modulos.avaliacao.repository;

import com.negociofechado.modulos.avaliacao.entity.AvaliacaoFoto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoFotoRepository extends JpaRepository<AvaliacaoFoto, Long> {

    List<AvaliacaoFoto> findByAvaliacaoIdOrderByOrdem(Long avaliacaoId);

    int countByAvaliacaoId(Long avaliacaoId);

    void deleteByAvaliacaoId(Long avaliacaoId);
}

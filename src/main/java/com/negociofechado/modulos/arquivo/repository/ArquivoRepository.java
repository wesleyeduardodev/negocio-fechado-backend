package com.negociofechado.modulos.arquivo.repository;

import com.negociofechado.modulos.arquivo.entity.Arquivo;
import com.negociofechado.modulos.arquivo.entity.TipoEntidade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArquivoRepository extends JpaRepository<Arquivo, Long> {

    List<Arquivo> findByTipoEntidadeAndEntidadeIdOrderByOrdem(TipoEntidade tipoEntidade, Long entidadeId);

    int countByTipoEntidadeAndEntidadeId(TipoEntidade tipoEntidade, Long entidadeId);

    void deleteByTipoEntidadeAndEntidadeId(TipoEntidade tipoEntidade, Long entidadeId);

    List<Arquivo> findByTipoEntidadeAndEntidadeId(TipoEntidade tipoEntidade, Long entidadeId);
}

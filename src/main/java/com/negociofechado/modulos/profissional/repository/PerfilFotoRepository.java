package com.negociofechado.modulos.profissional.repository;

import com.negociofechado.modulos.profissional.entity.PerfilFoto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerfilFotoRepository extends JpaRepository<PerfilFoto, Long> {

    List<PerfilFoto> findByPerfilIdOrderByOrdem(Long perfilId);

    int countByPerfilId(Long perfilId);

    void deleteByPerfilId(Long perfilId);
}

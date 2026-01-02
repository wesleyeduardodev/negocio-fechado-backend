package com.negociofechado.modulos.usuario.repository;

import java.util.Optional;

import com.negociofechado.modulos.usuario.entity.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCelular(String celular);

    boolean existsByCelular(String celular);

}

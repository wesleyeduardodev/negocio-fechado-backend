package com.negociofechado.modulos.usuario.entity;

import java.time.LocalDateTime;

import com.negociofechado.comum.entity.Endereco;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String celular;

    @Column(nullable = false)
    private String senha;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Embedded
    private Endereco endereco;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @Builder.Default
    @Column(name = "modo_preferido", nullable = false, length = 20)
    private String modoPreferido = "cliente";

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
    }

}

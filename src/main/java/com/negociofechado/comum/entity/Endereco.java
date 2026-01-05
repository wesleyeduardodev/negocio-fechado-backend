package com.negociofechado.comum.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    @Column(name = "uf", nullable = false, length = 2)
    private String uf;

    @Column(name = "cidade_ibge_id", nullable = false)
    private Integer cidadeIbgeId;

    @Column(name = "cidade_nome", nullable = false)
    private String cidadeNome;

    @Column(name = "bairro", nullable = false)
    private String bairro;
}

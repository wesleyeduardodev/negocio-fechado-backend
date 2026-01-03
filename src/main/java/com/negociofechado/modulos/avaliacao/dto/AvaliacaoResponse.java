package com.negociofechado.modulos.avaliacao.dto;

import com.negociofechado.modulos.avaliacao.entity.Avaliacao;

import java.time.LocalDateTime;

public record AvaliacaoResponse(
    Long id,
    Integer nota,
    String comentario,
    String clienteNome,
    LocalDateTime criadoEm
) {
    public static AvaliacaoResponse fromEntity(Avaliacao avaliacao) {
        return new AvaliacaoResponse(
            avaliacao.getId(),
            avaliacao.getNota(),
            avaliacao.getComentario(),
            avaliacao.getCliente().getNome(),
            avaliacao.getCriadoEm()
        );
    }
}

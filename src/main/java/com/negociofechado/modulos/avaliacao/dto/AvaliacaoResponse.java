package com.negociofechado.modulos.avaliacao.dto;
import com.negociofechado.modulos.avaliacao.entity.Avaliacao;
import java.time.LocalDateTime;
import java.util.List;

public record AvaliacaoResponse(
    Long id,
    Integer nota,
    String comentario,
    String clienteNome,
    LocalDateTime criadoEm,
    List<String> fotos
) {
    public static AvaliacaoResponse fromEntity(Avaliacao avaliacao, List<String> fotos) {
        return new AvaliacaoResponse(
            avaliacao.getId(),
            avaliacao.getNota(),
            avaliacao.getComentario(),
            avaliacao.getCliente().getNome(),
            avaliacao.getCriadoEm(),
            fotos
        );
    }
}

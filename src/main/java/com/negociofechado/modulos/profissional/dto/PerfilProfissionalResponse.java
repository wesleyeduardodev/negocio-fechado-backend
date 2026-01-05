package com.negociofechado.modulos.profissional.dto;
import java.time.LocalDateTime;
import java.util.List;

public record PerfilProfissionalResponse(
        Long id,
        Long usuarioId,
        String nome,
        String fotoUrl,
        String bio,
        String uf,
        String cidadeNome,
        String bairro,
        List<CategoriaResumoResponse> categorias,
        Double mediaAvaliacoes,
        Integer totalAvaliacoes,
        Boolean ativo,
        LocalDateTime criadoEm
) {}

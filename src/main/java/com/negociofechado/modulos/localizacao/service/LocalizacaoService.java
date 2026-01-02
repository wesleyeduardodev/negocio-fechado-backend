package com.negociofechado.modulos.localizacao.service;

import java.util.List;

import com.negociofechado.comum.entity.Endereco;
import com.negociofechado.modulos.localizacao.client.IbgeClient;
import com.negociofechado.modulos.localizacao.dto.CidadeResponse;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocalizacaoService {

    private final IbgeClient ibgeClient;

    public List<CidadeResponse> listarCidadesPorUf(String uf) {
        return ibgeClient.buscarCidadesPorUf(uf);
    }

    public Endereco criarEndereco(String uf, Integer cidadeIbgeId, String cidadeNome, String bairro) {
        return Endereco.builder()
                .uf(uf)
                .cidadeIbgeId(cidadeIbgeId)
                .cidadeNome(cidadeNome)
                .bairro(bairro)
                .build();
    }

}

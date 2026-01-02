package com.negociofechado.modulos.localizacao.client;

import java.util.Arrays;
import java.util.List;

import com.negociofechado.modulos.localizacao.dto.CidadeResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class IbgeClient {

    private static final String IBGE_API_URL = "https://servicodados.ibge.gov.br/api/v1/localidades";

    private final RestClient restClient;

    public IbgeClient() {
        this.restClient = RestClient.builder()
                .baseUrl(IBGE_API_URL)
                .build();
    }

    public List<CidadeResponse> buscarCidadesPorUf(String uf) {
        IbgeMunicipioResponse[] response = restClient.get()
                .uri("/estados/{uf}/municipios?orderBy=nome", uf)
                .retrieve()
                .body(IbgeMunicipioResponse[].class);

        if (response == null) {
            return List.of();
        }

        return Arrays.stream(response)
                .map(m -> new CidadeResponse(m.id(), m.nome(), uf.toUpperCase()))
                .toList();
    }

    public IbgeMunicipioDetalhadoResponse buscarMunicipio(Long municipioId) {
        return restClient.get()
                .uri("/municipios/{id}", municipioId)
                .retrieve()
                .body(IbgeMunicipioDetalhadoResponse.class);
    }

    public IbgeDistritoDetalhadoResponse buscarDistrito(Long distritoId) {
        return restClient.get()
                .uri("/distritos/{id}", distritoId)
                .retrieve()
                .body(IbgeDistritoDetalhadoResponse.class);
    }

    public record IbgeMunicipioResponse(Long id, String nome) {}

    public record IbgeDistritoResponse(Long id, String nome) {}

    public record IbgeDistritoDetalhadoResponse(Long id, String nome) {}

    public record IbgeMunicipioDetalhadoResponse(
            Long id,
            String nome,
            IbgeMicrorregiao microrregiao
    ) {
        public String getUf() {
            return microrregiao.mesorregiao().UF().sigla();
        }
    }

    public record IbgeMicrorregiao(IbgeMesorregiao mesorregiao) {}

    public record IbgeMesorregiao(IbgeUF UF) {}

    public record IbgeUF(Integer id, String sigla, String nome) {}

}

package com.negociofechado.modulos.localizacao.controller;

import java.util.List;

import com.negociofechado.modulos.localizacao.dto.CidadeResponse;
import com.negociofechado.modulos.localizacao.service.LocalizacaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cidades")
@RequiredArgsConstructor
@Tag(name = "Cidades", description = "Endpoints de localização")
public class CidadeController {

    private final LocalizacaoService localizacaoService;

    @GetMapping
    @Operation(summary = "Listar cidades por UF", description = "Retorna cidades do IBGE filtradas por UF")
    public List<CidadeResponse> listarCidades(@RequestParam(defaultValue = "MA") String uf) {
        return localizacaoService.listarCidadesPorUf(uf);
    }

}

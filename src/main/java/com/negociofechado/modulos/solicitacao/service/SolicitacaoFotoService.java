package com.negociofechado.modulos.solicitacao.service;

import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.comum.exception.RecursoNaoEncontradoException;
import com.negociofechado.comum.storage.StorageService;
import com.negociofechado.comum.storage.dto.UploadRequest;
import com.negociofechado.comum.storage.dto.UploadResult;
import com.negociofechado.modulos.arquivo.dto.ArquivoResponse;
import com.negociofechado.modulos.arquivo.dto.ProcessedImage;
import com.negociofechado.modulos.arquivo.service.ImagemService;
import com.negociofechado.modulos.solicitacao.entity.Solicitacao;
import com.negociofechado.modulos.solicitacao.entity.SolicitacaoFoto;
import com.negociofechado.modulos.solicitacao.repository.SolicitacaoFotoRepository;
import com.negociofechado.modulos.solicitacao.repository.SolicitacaoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SolicitacaoFotoService {

    private final SolicitacaoFotoRepository solicitacaoFotoRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final StorageService storageService;
    private final ImagemService imagemService;

    private static final int MAX_FOTOS = 5;

    @Transactional
    public List<ArquivoResponse> uploadFotos(Long solicitacaoId, List<MultipartFile> fotos) {
        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação", solicitacaoId));

        int fotosExistentes = solicitacaoFotoRepository.countBySolicitacaoId(solicitacaoId);

        if (fotosExistentes + fotos.size() > MAX_FOTOS) {
            throw new NegocioException("Máximo de " + MAX_FOTOS + " fotos permitidas");
        }

        List<SolicitacaoFoto> fotosSalvas = new ArrayList<>();
        int ordem = fotosExistentes;

        for (MultipartFile foto : fotos) {
            imagemService.validar(foto);
            ProcessedImage processed = imagemService.processar(foto);

            String fileName = gerarNomeArquivo(++ordem, processed.extensao());
            String directory = "solicitacoes/" + solicitacaoId;

            UploadResult result = storageService.upload(new UploadRequest(
                    processed.bytes(),
                    fileName,
                    processed.contentType(),
                    directory,
                    Map.of("originalName", foto.getOriginalFilename() != null ? foto.getOriginalFilename() : fileName)
            ));

            SolicitacaoFoto solicitacaoFoto = SolicitacaoFoto.builder()
                    .solicitacao(solicitacao)
                    .path(result.path())
                    .url(result.url())
                    .nomeOriginal(foto.getOriginalFilename() != null ? foto.getOriginalFilename() : fileName)
                    .contentType(processed.contentType())
                    .tamanho(result.size())
                    .largura(processed.largura())
                    .altura(processed.altura())
                    .ordem(ordem)
                    .build();

            fotosSalvas.add(solicitacaoFotoRepository.save(solicitacaoFoto));
        }

        return fotosSalvas.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ArquivoResponse> listarFotos(Long solicitacaoId) {
        return solicitacaoFotoRepository.findBySolicitacaoIdOrderByOrdem(solicitacaoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deletarFoto(Long fotoId) {
        SolicitacaoFoto foto = solicitacaoFotoRepository.findById(fotoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Foto", fotoId));

        storageService.delete(foto.getPath());
        solicitacaoFotoRepository.delete(foto);
    }

    @Transactional
    public void deletarTodasFotos(Long solicitacaoId) {
        List<SolicitacaoFoto> fotos = solicitacaoFotoRepository.findBySolicitacaoIdOrderByOrdem(solicitacaoId);

        if (!fotos.isEmpty()) {
            List<String> paths = fotos.stream()
                    .map(SolicitacaoFoto::getPath)
                    .toList();
            storageService.deleteAll(paths);
            solicitacaoFotoRepository.deleteAll(fotos);
        }
    }

    private String gerarNomeArquivo(int ordem, String extensao) {
        return String.format("foto_%03d.%s", ordem, extensao);
    }

    private ArquivoResponse toResponse(SolicitacaoFoto foto) {
        return new ArquivoResponse(
                foto.getId(),
                foto.getUrl(),
                foto.getNomeOriginal(),
                foto.getTamanho(),
                foto.getLargura(),
                foto.getAltura(),
                foto.getOrdem()
        );
    }
}

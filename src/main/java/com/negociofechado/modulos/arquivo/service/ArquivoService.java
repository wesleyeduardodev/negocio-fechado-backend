package com.negociofechado.modulos.arquivo.service;

import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.comum.exception.RecursoNaoEncontradoException;
import com.negociofechado.comum.storage.StorageService;
import com.negociofechado.comum.storage.dto.UploadRequest;
import com.negociofechado.comum.storage.dto.UploadResult;
import com.negociofechado.modulos.arquivo.dto.ArquivoResponse;
import com.negociofechado.modulos.arquivo.dto.ProcessedImage;
import com.negociofechado.modulos.arquivo.entity.Arquivo;
import com.negociofechado.modulos.arquivo.entity.TipoEntidade;
import com.negociofechado.modulos.arquivo.repository.ArquivoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArquivoService {

    private final ArquivoRepository arquivoRepository;
    private final StorageService storageService;
    private final ImagemService imagemService;

    private static final int MAX_FOTOS_SOLICITACAO = 5;
    private static final int MAX_FOTOS_AVALIACAO = 3;
    private static final int MAX_FOTOS_PORTFOLIO = 10;

    @Transactional
    public List<ArquivoResponse> uploadFotosSolicitacao(Long solicitacaoId, List<MultipartFile> fotos) {
        return uploadFotos(TipoEntidade.SOLICITACAO, solicitacaoId, fotos, MAX_FOTOS_SOLICITACAO, "solicitacoes");
    }

    @Transactional
    public List<ArquivoResponse> uploadFotosAvaliacao(Long avaliacaoId, List<MultipartFile> fotos) {
        return uploadFotos(TipoEntidade.AVALIACAO, avaliacaoId, fotos, MAX_FOTOS_AVALIACAO, "avaliacoes");
    }

    @Transactional
    public List<ArquivoResponse> uploadFotosPortfolio(Long profissionalId, List<MultipartFile> fotos) {
        return uploadFotos(TipoEntidade.PORTFOLIO, profissionalId, fotos, MAX_FOTOS_PORTFOLIO, "portfolios");
    }

    @Transactional
    public ArquivoResponse uploadAvatar(Long usuarioId, MultipartFile foto) {
        deletarArquivos(TipoEntidade.USUARIO, usuarioId);

        imagemService.validar(foto);
        ProcessedImage processed = imagemService.processar(foto);

        String directory = "usuarios/" + usuarioId;
        String fileName = "avatar.jpg";

        UploadResult result = storageService.upload(new UploadRequest(
            processed.bytes(),
            fileName,
            processed.contentType(),
            directory,
            Map.of("originalName", foto.getOriginalFilename() != null ? foto.getOriginalFilename() : "avatar")
        ));

        Arquivo arquivo = Arquivo.builder()
            .path(result.path())
            .url(result.url())
            .nomeOriginal(foto.getOriginalFilename() != null ? foto.getOriginalFilename() : "avatar.jpg")
            .contentType(processed.contentType())
            .tamanho(result.size())
            .largura(processed.largura())
            .altura(processed.altura())
            .ordem(0)
            .tipoEntidade(TipoEntidade.USUARIO)
            .entidadeId(usuarioId)
            .build();

        arquivoRepository.save(arquivo);

        return toResponse(arquivo);
    }

    @Transactional(readOnly = true)
    public List<ArquivoResponse> listarFotosSolicitacao(Long solicitacaoId) {
        return listarArquivos(TipoEntidade.SOLICITACAO, solicitacaoId);
    }

    @Transactional(readOnly = true)
    public List<ArquivoResponse> listarFotosAvaliacao(Long avaliacaoId) {
        return listarArquivos(TipoEntidade.AVALIACAO, avaliacaoId);
    }

    @Transactional(readOnly = true)
    public List<ArquivoResponse> listarFotosPortfolio(Long profissionalId) {
        return listarArquivos(TipoEntidade.PORTFOLIO, profissionalId);
    }

    @Transactional(readOnly = true)
    public ArquivoResponse buscarAvatar(Long usuarioId) {
        List<Arquivo> arquivos = arquivoRepository.findByTipoEntidadeAndEntidadeId(TipoEntidade.USUARIO, usuarioId);
        if (arquivos.isEmpty()) {
            return null;
        }
        return toResponse(arquivos.get(0));
    }

    @Transactional
    public void deletarFoto(Long fotoId) {
        Arquivo arquivo = arquivoRepository.findById(fotoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Arquivo", fotoId));

        storageService.delete(arquivo.getPath());
        arquivoRepository.delete(arquivo);
    }

    @Transactional
    public void deletarFotosSolicitacao(Long solicitacaoId) {
        deletarArquivos(TipoEntidade.SOLICITACAO, solicitacaoId);
    }

    @Transactional
    public void deletarAvatar(Long usuarioId) {
        deletarArquivos(TipoEntidade.USUARIO, usuarioId);
    }

    private List<ArquivoResponse> uploadFotos(
            TipoEntidade tipo,
            Long entidadeId,
            List<MultipartFile> fotos,
            int maxFotos,
            String directoryPrefix) {

        int fotosExistentes = arquivoRepository.countByTipoEntidadeAndEntidadeId(tipo, entidadeId);

        if (fotosExistentes + fotos.size() > maxFotos) {
            throw new NegocioException("Maximo de " + maxFotos + " fotos permitidas");
        }

        List<Arquivo> arquivosSalvos = new ArrayList<>();
        int ordem = fotosExistentes;

        for (MultipartFile foto : fotos) {
            imagemService.validar(foto);
            ProcessedImage processed = imagemService.processar(foto);

            String fileName = gerarNomeArquivo(++ordem, processed.extensao());
            String directory = directoryPrefix + "/" + entidadeId;

            UploadResult result = storageService.upload(new UploadRequest(
                processed.bytes(),
                fileName,
                processed.contentType(),
                directory,
                Map.of("originalName", foto.getOriginalFilename() != null ? foto.getOriginalFilename() : fileName)
            ));

            Arquivo arquivo = Arquivo.builder()
                .path(result.path())
                .url(result.url())
                .nomeOriginal(foto.getOriginalFilename() != null ? foto.getOriginalFilename() : fileName)
                .contentType(processed.contentType())
                .tamanho(result.size())
                .largura(processed.largura())
                .altura(processed.altura())
                .ordem(ordem)
                .tipoEntidade(tipo)
                .entidadeId(entidadeId)
                .build();

            arquivosSalvos.add(arquivoRepository.save(arquivo));
        }

        return arquivosSalvos.stream()
            .map(this::toResponse)
            .toList();
    }

    private List<ArquivoResponse> listarArquivos(TipoEntidade tipo, Long entidadeId) {
        return arquivoRepository.findByTipoEntidadeAndEntidadeIdOrderByOrdem(tipo, entidadeId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private void deletarArquivos(TipoEntidade tipo, Long entidadeId) {
        List<Arquivo> arquivos = arquivoRepository.findByTipoEntidadeAndEntidadeId(tipo, entidadeId);

        if (!arquivos.isEmpty()) {
            List<String> paths = arquivos.stream()
                .map(Arquivo::getPath)
                .toList();
            storageService.deleteAll(paths);
            arquivoRepository.deleteAll(arquivos);
        }
    }

    private String gerarNomeArquivo(int ordem, String extensao) {
        return String.format("foto_%03d.%s", ordem, extensao);
    }

    private ArquivoResponse toResponse(Arquivo arquivo) {
        return new ArquivoResponse(
            arquivo.getId(),
            arquivo.getUrl(),
            arquivo.getNomeOriginal(),
            arquivo.getTamanho(),
            arquivo.getLargura(),
            arquivo.getAltura(),
            arquivo.getOrdem()
        );
    }
}

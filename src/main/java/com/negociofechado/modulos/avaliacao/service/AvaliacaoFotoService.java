package com.negociofechado.modulos.avaliacao.service;
import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.comum.exception.RecursoNaoEncontradoException;
import com.negociofechado.comum.storage.StorageService;
import com.negociofechado.comum.storage.dto.UploadRequest;
import com.negociofechado.comum.storage.dto.UploadResult;
import com.negociofechado.modulos.arquivo.dto.ArquivoResponse;
import com.negociofechado.modulos.arquivo.dto.ProcessedImage;
import com.negociofechado.modulos.arquivo.service.ImagemService;
import com.negociofechado.modulos.avaliacao.entity.Avaliacao;
import com.negociofechado.modulos.avaliacao.entity.AvaliacaoFoto;
import com.negociofechado.modulos.avaliacao.repository.AvaliacaoFotoRepository;
import com.negociofechado.modulos.avaliacao.repository.AvaliacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AvaliacaoFotoService {

    private final AvaliacaoFotoRepository avaliacaoFotoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final StorageService storageService;
    private final ImagemService imagemService;

    private static final int MAX_FOTOS = 5;

    @Transactional
    public List<ArquivoResponse> uploadFotos(Long avaliacaoId, List<MultipartFile> fotos) {

        Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Avaliacao", avaliacaoId));

        int fotosExistentes = avaliacaoFotoRepository.countByAvaliacaoId(avaliacaoId);

        if (fotosExistentes + fotos.size() > MAX_FOTOS) {
            throw new NegocioException("Maximo de " + MAX_FOTOS + " fotos permitidas");
        }

        List<AvaliacaoFoto> fotosSalvas = new ArrayList<>();
        int ordem = fotosExistentes;

        for (MultipartFile foto : fotos) {
            imagemService.validar(foto);
            ProcessedImage processed = imagemService.processar(foto);

            String fileName = gerarNomeArquivo(++ordem, processed.extensao());
            String directory = "avaliacoes/" + avaliacaoId;

            UploadResult result = storageService.upload(new UploadRequest(
                    processed.bytes(),
                    fileName,
                    processed.contentType(),
                    directory,
                    Map.of("originalName", foto.getOriginalFilename() != null ? foto.getOriginalFilename() : fileName)
            ));

            AvaliacaoFoto avaliacaoFoto = AvaliacaoFoto.builder()
                    .avaliacao(avaliacao)
                    .path(result.path())
                    .url(result.url())
                    .nomeOriginal(foto.getOriginalFilename() != null ? foto.getOriginalFilename() : fileName)
                    .contentType(processed.contentType())
                    .tamanho(result.size())
                    .largura(processed.largura())
                    .altura(processed.altura())
                    .ordem(ordem)
                    .build();

            fotosSalvas.add(avaliacaoFotoRepository.save(avaliacaoFoto));
        }

        return fotosSalvas.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ArquivoResponse> listarFotos(Long avaliacaoId) {
        return avaliacaoFotoRepository.findByAvaliacaoIdOrderByOrdem(avaliacaoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> listarUrlsFotos(Long avaliacaoId) {
        return avaliacaoFotoRepository.findByAvaliacaoIdOrderByOrdem(avaliacaoId)
                .stream()
                .map(AvaliacaoFoto::getUrl)
                .toList();
    }

    @Transactional
    public void deletarFoto(Long fotoId) {
        AvaliacaoFoto foto = avaliacaoFotoRepository.findById(fotoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Foto", fotoId));

        storageService.delete(foto.getPath());
        avaliacaoFotoRepository.delete(foto);
    }

    @Transactional
    public void deletarTodasFotos(Long avaliacaoId) {
        List<AvaliacaoFoto> fotos = avaliacaoFotoRepository.findByAvaliacaoIdOrderByOrdem(avaliacaoId);

        if (!fotos.isEmpty()) {
            List<String> paths = fotos.stream()
                    .map(AvaliacaoFoto::getPath)
                    .toList();
            storageService.deleteAll(paths);
            avaliacaoFotoRepository.deleteAll(fotos);
        }
    }

    public Long getAvaliacaoClienteId(Long avaliacaoId) {
        Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Avaliacao", avaliacaoId));
        return avaliacao.getCliente().getId();
    }

    private String gerarNomeArquivo(int ordem, String extensao) {
        return String.format("foto_%03d.%s", ordem, extensao);
    }

    private ArquivoResponse toResponse(AvaliacaoFoto foto) {
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

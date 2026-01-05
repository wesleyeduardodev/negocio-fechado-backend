package com.negociofechado.modulos.profissional.service;
import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.comum.exception.RecursoNaoEncontradoException;
import com.negociofechado.comum.storage.StorageService;
import com.negociofechado.comum.storage.dto.UploadRequest;
import com.negociofechado.comum.storage.dto.UploadResult;
import com.negociofechado.modulos.arquivo.dto.ArquivoResponse;
import com.negociofechado.modulos.arquivo.dto.ProcessedImage;
import com.negociofechado.modulos.arquivo.service.ImagemService;
import com.negociofechado.modulos.profissional.entity.PerfilProfissional;
import com.negociofechado.modulos.profissional.entity.PerfilFoto;
import com.negociofechado.modulos.profissional.repository.PerfilProfissionalRepository;
import com.negociofechado.modulos.profissional.repository.PerfilFotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerfilFotoService {

    private final PerfilFotoRepository perfilFotoRepository;
    private final PerfilProfissionalRepository perfilProfissionalRepository;
    private final StorageService storageService;
    private final ImagemService imagemService;

    private static final int MAX_FOTOS = 10;

    @Transactional
    public List<ArquivoResponse> uploadFotos(Long perfilId, List<MultipartFile> fotos) {
        PerfilProfissional perfil = perfilProfissionalRepository.findById(perfilId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("PerfilProfissional", perfilId));

        int fotosExistentes = perfilFotoRepository.countByPerfilId(perfilId);

        if (fotosExistentes + fotos.size() > MAX_FOTOS) {
            throw new NegocioException("Maximo de " + MAX_FOTOS + " fotos permitidas no portfolio");
        }

        List<PerfilFoto> fotosSalvas = new ArrayList<>();
        int ordem = fotosExistentes;

        for (MultipartFile foto : fotos) {
            imagemService.validar(foto);
            ProcessedImage processed = imagemService.processar(foto);

            String fileName = gerarNomeArquivo(++ordem, processed.extensao());
            String directory = "profissionais/" + perfilId + "/portfolio";

            UploadResult result = storageService.upload(new UploadRequest(
                    processed.bytes(),
                    fileName,
                    processed.contentType(),
                    directory,
                    Map.of("originalName", foto.getOriginalFilename() != null ? foto.getOriginalFilename() : fileName)
            ));

            PerfilFoto perfilFoto = PerfilFoto.builder()
                    .perfil(perfil)
                    .path(result.path())
                    .url(result.url())
                    .nomeOriginal(foto.getOriginalFilename() != null ? foto.getOriginalFilename() : fileName)
                    .contentType(processed.contentType())
                    .tamanho(result.size())
                    .largura(processed.largura())
                    .altura(processed.altura())
                    .ordem(ordem)
                    .build();

            fotosSalvas.add(perfilFotoRepository.save(perfilFoto));
        }

        return fotosSalvas.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ArquivoResponse> listarFotos(Long perfilId) {
        return perfilFotoRepository.findByPerfilIdOrderByOrdem(perfilId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> listarUrlsFotos(Long perfilId) {
        return perfilFotoRepository.findByPerfilIdOrderByOrdem(perfilId)
                .stream()
                .map(PerfilFoto::getUrl)
                .toList();
    }

    @Transactional(readOnly = true)
    public int contarFotos(Long perfilId) {
        return perfilFotoRepository.countByPerfilId(perfilId);
    }

    @Transactional
    public void deletarFoto(Long fotoId) {
        PerfilFoto foto = perfilFotoRepository.findById(fotoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Foto", fotoId));

        storageService.delete(foto.getPath());
        perfilFotoRepository.delete(foto);
    }

    @Transactional
    public void deletarTodasFotos(Long perfilId) {
        List<PerfilFoto> fotos = perfilFotoRepository.findByPerfilIdOrderByOrdem(perfilId);

        if (!fotos.isEmpty()) {
            List<String> paths = fotos.stream()
                    .map(PerfilFoto::getPath)
                    .toList();
            storageService.deleteAll(paths);
            perfilFotoRepository.deleteAll(fotos);
        }
    }

    public Long getPerfilUsuarioId(Long perfilId) {
        PerfilProfissional perfil = perfilProfissionalRepository.findById(perfilId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("PerfilProfissional", perfilId));
        return perfil.getUsuario().getId();
    }

    public Long getPerfilIdByUsuarioId(Long usuarioId) {
        PerfilProfissional perfil = perfilProfissionalRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NegocioException("Voce nao possui um perfil profissional"));
        return perfil.getId();
    }

    private String gerarNomeArquivo(int ordem, String extensao) {
        return String.format("portfolio_%03d.%s", ordem, extensao);
    }

    private ArquivoResponse toResponse(PerfilFoto foto) {
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

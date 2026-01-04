package com.negociofechado.modulos.usuario.service;

import com.negociofechado.comum.storage.StorageService;
import com.negociofechado.comum.storage.dto.UploadRequest;
import com.negociofechado.comum.storage.dto.UploadResult;
import com.negociofechado.modulos.arquivo.dto.ProcessedImage;
import com.negociofechado.modulos.arquivo.service.ImagemService;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsuarioAvatarService {

    private final StorageService storageService;
    private final ImagemService imagemService;

    public String uploadAvatar(Long usuarioId, MultipartFile foto) {
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

        return result.url();
    }

    public void deletarAvatar(Long usuarioId) {
        String path = "usuarios/" + usuarioId + "/avatar.jpg";
        storageService.delete(path);
    }
}

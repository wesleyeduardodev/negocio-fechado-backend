package com.negociofechado.comum.storage.dto;
import java.util.Map;

public record UploadRequest(
    byte[] content,
    String fileName,
    String contentType,
    String directory,
    Map<String, String> metadata
) {}

package com.negociofechado.comum.storage.dto;

public record UploadResult(
    String path,
    String url,
    String fileName,
    long size,
    String contentType
) {}

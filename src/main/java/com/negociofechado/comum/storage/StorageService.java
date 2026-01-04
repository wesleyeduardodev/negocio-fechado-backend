package com.negociofechado.comum.storage;

import com.negociofechado.comum.storage.dto.UploadRequest;
import com.negociofechado.comum.storage.dto.UploadResult;

import java.time.Duration;
import java.util.List;

public interface StorageService {

    UploadResult upload(UploadRequest request);

    void delete(String path);

    void deleteAll(List<String> paths);

    String generateSignedUrl(String path, Duration expiration);

    boolean exists(String path);
}

package com.negociofechado.comum.storage.impl;
import com.negociofechado.comum.storage.StorageException;
import com.negociofechado.comum.storage.StorageService;
import com.negociofechado.comum.storage.dto.UploadRequest;
import com.negociofechado.comum.storage.dto.UploadResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import lombok.RequiredArgsConstructor;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${storage.s3.bucket}")
    private String bucket;

    @Value("${storage.s3.region}")
    private String region;

    @Override
    public UploadResult upload(UploadRequest request) {
        String path = buildPath(request.directory(), request.fileName());

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(path)
                    .contentType(request.contentType())
                    .metadata(request.metadata())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(request.content()));

            String url = buildPublicUrl(path);

            return new UploadResult(
                    path,
                    url,
                    request.fileName(),
                    request.content().length,
                    request.contentType()
            );
        } catch (Exception e) {
            throw new StorageException("Falha ao fazer upload para S3", e);
        }
    }

    @Override
    public void delete(String path) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(path)
                    .build());
        } catch (Exception e) {
            throw new StorageException("Falha ao deletar arquivo do S3", e);
        }
    }

    @Override
    public void deleteAll(List<String> paths) {
        if (paths.isEmpty()) return;

        try {
            List<ObjectIdentifier> keys = paths.stream()
                    .map(p -> ObjectIdentifier.builder().key(p).build())
                    .toList();

            s3Client.deleteObjects(DeleteObjectsRequest.builder()
                    .bucket(bucket)
                    .delete(Delete.builder().objects(keys).build())
                    .build());
        } catch (Exception e) {
            throw new StorageException("Falha ao deletar arquivos do S3", e);
        }
    }

    @Override
    public String generateSignedUrl(String path, Duration expiration) {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(path)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(expiration)
                    .getObjectRequest(getRequest)
                    .build();

            return s3Presigner.presignGetObject(presignRequest)
                    .url()
                    .toString();
        } catch (Exception e) {
            throw new StorageException("Falha ao gerar URL assinada", e);
        }
    }

    @Override
    public boolean exists(String path) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(path)
                    .build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    private String buildPath(String directory, String fileName) {
        return directory + "/" + fileName;
    }

    private String buildPublicUrl(String path) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, path);
    }
}

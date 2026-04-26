package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.service.IS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements IS3Service {
    @Value("${AWS_S3_BUCKET}")
    private String BUCKET;

    private final S3Presigner preSigner;

    @Override
    public String getPresignUrl(String key) {
        int PRESIGN_EXPIRES = 5;
        GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(PRESIGN_EXPIRES))
                .getObjectRequest(r -> r.bucket(BUCKET).key(key))
                .build();

        PresignedGetObjectRequest presigned = preSigner.presignGetObject(request);
        log.info("[S3][getPresign]: Presign success: {}", presigned.url());
        return presigned.url().toString();
    }

    @Override
    public String upload(MultipartFile file, String key) {
        return "";
    }
}

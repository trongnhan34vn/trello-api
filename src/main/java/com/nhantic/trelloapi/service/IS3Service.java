package com.nhantic.trelloapi.service;

import org.springframework.web.multipart.MultipartFile;

public interface IS3Service {
    String getPresignUrl(String key);
    String upload(MultipartFile file, String key);
}

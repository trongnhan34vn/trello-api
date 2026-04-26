package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.dto.response.ImageResponse;
import com.nhantic.trelloapi.entity.Image;
import com.nhantic.trelloapi.repository.IImageRepository;
import com.nhantic.trelloapi.service.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements IImageService {
    private final IImageRepository imageRepository;

    @Value("${AWS_CLOUDFRONT}")
    private String CLOUDFRONT;

    @Override
    public List<ImageResponse> findAll() {
        try {
            List< Image> images = imageRepository.findAll();
            return images.stream().map(i -> (
                    ImageResponse.builder()
                            .id(i.getId())
                            .url(CLOUDFRONT + i.getKey())
                            .build()
            )).toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}

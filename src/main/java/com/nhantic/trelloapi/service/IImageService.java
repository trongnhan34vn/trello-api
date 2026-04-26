package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.ImageResponse;

import java.util.List;

public interface IImageService {
    List<ImageResponse> findAll();
}

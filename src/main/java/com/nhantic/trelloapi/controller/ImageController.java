package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.response.ImageResponse;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class ImageController {
    private final IImageService imageService;
    private final MessageResolver mr;
    @GetMapping()
    public ResponseEntity<?> list() {
        List<ImageResponse> images = imageService.findAll();
        Response res = Response.builder()
                .code(SuccessMessageCode.IMAGE_FOUND)
                .message(mr.resolve(SuccessMessageCode.IMAGE_FOUND))
                .data(images)
                .build();
        return ResponseEntity.ok(res);
    }
}

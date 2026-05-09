package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.response.ImageResponse;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
@Tag(name = "Image", description = "Endpoints for retrieving images")
public class ImageController {
    private final IImageService imageService;
    private final MessageResolver mr;

    @Operation(
            summary = "Get all images",
            description = "Retrieve a list of all available images for board backgrounds",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Images retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Response.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized"
                    )
            }
    )
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

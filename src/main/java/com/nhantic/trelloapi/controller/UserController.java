package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.dto.response.UserResponse;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IUserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints for user entity")
public class UserController {
    private final MessageResolver mr;
    private final IUserQueryService userQueryService;


    @Operation(
            summary = "Get current user",
            description = "Retrieve information of the currently authenticated user using access token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Response.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or missing access token"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal Jwt jwt) {
        UserResponse userResponse = userQueryService.getMe(jwt);
        Response res = Response.builder()
                .code(SuccessMessageCode.USER_FOUND)
                .message(mr.resolve(SuccessMessageCode.USER_FOUND))
                .success(true)
                .data(userResponse)
                .build();
        return ResponseEntity.ok(res);
    }
}

package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.request.*;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.dto.response.TokenResponse;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {
    private final IAuthService authService;
    private final MessageResolver mr;

    private final String ACCESS_TOKEN_FIELD_NAME = "access_token";
    private final String REFRESH_TOKEN_FIELD_NAME = "refresh_token";
    private final String COOKIE_ROOT_PATH = "/";

    @Operation(
            summary = "Sign in user",
            description = "Authenticate user with username and password. Returns access token and refresh token in HTTP-only cookies.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Sign in credentials",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SignInRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully signed in",
                            content = @Content(schema = @Schema(implementation = Response.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid username or password",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        TokenResponse token = authService.signIn(signInRequest);

        ResponseCookie accessTokenCookie = ResponseCookie.from(ACCESS_TOKEN_FIELD_NAME, token.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(token.getExpiresIn())
                .path(COOKIE_ROOT_PATH)
                .build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_FIELD_NAME, token.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(token.getRefreshExpiresIn())
                .path(COOKIE_ROOT_PATH)
                .build();

        Response res = Response.builder()
                .message(mr.resolve(SuccessMessageCode.SIGN_IN_SUCCESS))
                .code(SuccessMessageCode.SIGN_IN_SUCCESS)
                .success(true)
                .build();

        return ResponseEntity.ok()
                .headers(headers -> {
                    headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
                    headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
                })
                .body(res);
    }

    @Operation(
            summary = "Sign up new user",
            description = "Register a new user. Cognito will send a confirmation code to email.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "User registration info",
                    content = @Content(schema = @Schema(implementation = SignUpRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User registered successfully",
                            content = @Content(schema = @Schema(implementation = Response.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input or user already exists"
                    )
            }
    )
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        authService.signUp(signUpRequest);
        Response res = Response.builder()
                .message(mr.resolve(SuccessMessageCode.SIGN_UP_SUCCESS))
                .success(true)
                .data(signUpRequest.getEmail())
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Confirm sign up",
            description = "Confirm user registration using the code sent to email",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Username and confirmation code",
                    content = @Content(schema = @Schema(implementation = ConfirmSignUpRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User confirmed successfully",
                            content = @Content(schema = @Schema(implementation = Response.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid confirmation code"
                    )
            }
    )
    @PostMapping("/confirm-sign-up")
    public ResponseEntity<?> confirmSignUp(@Valid @RequestBody ConfirmSignUpRequest confirmSignUpRequest) {
        authService.confirmSignUp(confirmSignUpRequest);
        Response res = Response.builder()
                .message(mr.resolve(SuccessMessageCode.CONFIRM_SIGN_UP_SUCCESS))
                .success(true)
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Refresh access token",
            description = "Generate new access token using refresh token stored in HttpOnly cookie",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Refresh token successfully",
                            content = @Content(schema = @Schema(implementation = Response.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - missing or invalid refresh token"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(value = REFRESH_TOKEN_FIELD_NAME, required = false) String refreshToken) {
        TokenResponse token = authService.refreshToken(refreshToken);
        ResponseCookie accessTokenCookie = ResponseCookie.from(ACCESS_TOKEN_FIELD_NAME, token.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .domain(".compute.amazonaws.com")
                .sameSite("None")
                .maxAge(token.getExpiresIn())
                .path(COOKIE_ROOT_PATH)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_FIELD_NAME, token.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .domain(".compute.amazonaws.com")
                .sameSite("None")
                .maxAge(token.getRefreshExpiresIn())
                .path(COOKIE_ROOT_PATH)
                .build();

        Response res = Response.builder()
                .message(mr.resolve(SuccessMessageCode.REFRESH_TOKEN_SUCCESS))
                .code(SuccessMessageCode.REFRESH_TOKEN_SUCCESS)
                .success(true)
                .build();

        return ResponseEntity.ok()
                .headers(headers -> {
                    headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
                    headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
                })
                .body(res);

    }

    @Operation(
            summary = "Resend confirmation code",
            description = "Resend the sign-up confirmation code to user's email",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Resend code payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ResendCodeRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Code resent successfully",
                            content = @Content(schema = @Schema(implementation = Response.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request or user not found"
                    )
            }
    )
    @PostMapping("/resend-code")
    public ResponseEntity<?> resendCode(@Valid @RequestBody ResendCodeRequest resendCodeRequest) {
        authService.resendCode(resendCodeRequest);
        Response res = Response.builder()
                .code(SuccessMessageCode.RESEND_CODE_SUCCESS)
                .message(mr.resolve(SuccessMessageCode.RESEND_CODE_SUCCESS))
                .data(resendCodeRequest.getUsername())
                .success(true)
                .build();
        return ResponseEntity.ok(res);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest req, @AuthenticationPrincipal Jwt jwt) {
        req.setAccessToken(jwt.getTokenValue());
        authService.changePassword(req);
        Response res = Response.builder()
                .code(SuccessMessageCode.CHANGE_PASSWORD_SUCCESS)
                .message(mr.resolve(SuccessMessageCode.CHANGE_PASSWORD_SUCCESS))
                .data(null)
                .success(true)
                .build();
        return ResponseEntity.ok(res);
    }
}

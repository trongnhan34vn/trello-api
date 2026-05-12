package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.dto.request.*;
import com.nhantic.trelloapi.dto.response.TokenResponse;
import com.nhantic.trelloapi.dto.response.UserCreateResponse;
import com.nhantic.trelloapi.exception.BadRequestException;
import com.nhantic.trelloapi.exception.InternalServerErrorException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IAuthService;
import com.nhantic.trelloapi.service.ICognitoService;
import com.nhantic.trelloapi.service.IUserCommandService;
import com.nhantic.trelloapi.service.IUserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final ICognitoService cognitoService;
    private final IUserCommandService userCommandService;
    private final IUserQueryService userQueryService;
    private final JwtDecoder jwtDecoder;
    private final MessageResolver mr;

    @Override
    @Transactional
    public TokenResponse signIn(SignInRequest signInRequest) {
        try {
            // sign in
            log.info("[Auth][SignIn]: {}", signInRequest.getEmail());

            CognitoSignInRequest cognitoSignInRequest = CognitoSignInRequest.builder()
                    .username(signInRequest.getEmail())
                    .password(signInRequest.getPassword())
                    .build();
            AuthenticationResultType authRes = cognitoService.signIn(cognitoSignInRequest);
            // save user
            String idToken = authRes.idToken();
            Jwt jwt = jwtDecoder.decode(idToken);
            provisionUserIfAbsent(jwt, UserCreateRequest.builder()
                    .email(signInRequest.getEmail())
                    .build());

            log.info("[Auth][SignIn]: Success");
            return buildTokenResponse(authRes);
        } catch (Exception e) {
            log.error("[Auth][SignIn]: Error {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void provisionUserIfAbsent(Jwt jwt, UserCreateRequest dto) {
        String FULL_NAME_COGNITO_FIELD = "custom:fullName";
        boolean isExistedUser = userQueryService.existsByCognitoId(jwt.getSubject());
        if (!isExistedUser) {
            log.info("[Auth][SignIn]: Create user");
            String fullName = jwt.getClaim(FULL_NAME_COGNITO_FIELD);
            dto.setFullName(fullName);
            dto.setCognitoId(jwt.getSubject());
            UserCreateResponse createdUser = userCommandService.create(dto);

            if (createdUser == null) {
                throw new InternalServerErrorException(
                        ErrorMessageCode.INTERNAL_SERVER_ERROR,
                        mr.resolve(ErrorMessageCode.INTERNAL_SERVER_ERROR)
                );
            }
        }
    }

    private TokenResponse buildTokenResponse(AuthenticationResultType auth) {
        int REFRESH_EXPIRES_IN = 86400000;
        return TokenResponse.builder()
                .accessToken(auth.accessToken())
                .refreshToken(auth.refreshToken())
                .tokenType(auth.tokenType())
                .refreshExpiresIn(REFRESH_EXPIRES_IN)
                .expiresIn(auth.expiresIn())
                .build();
    }

    @Override
    public void signUp(SignUpRequest signUpRequest) {
        try {
            log.info("[Auth][SignUp]: Start: {}", signUpRequest.getEmail());
            CognitoSignUpRequest cognitoSignUpDto = CognitoSignUpRequest.builder()
                    .username(signUpRequest.getEmail())
                    .password(signUpRequest.getPassword())
                    .fullName(signUpRequest.getFullName())
                    .build();
            cognitoService.signUp(cognitoSignUpDto);
            log.info("[Auth][SignUp]: Success");
        } catch (Exception e) {
            log.error("[SignUp]: Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void confirmSignUp(ConfirmSignUpRequest confirmSignUpRequest) {
        try {
            log.info("[Auth][ConfirmSignUp]: Start {}", confirmSignUpRequest.getUsername());
            CognitoConfirmSignUpRequest cognitoConfirmSignUpRequest = CognitoConfirmSignUpRequest.builder()
                    .username(confirmSignUpRequest.getUsername())
                    .confirmationCode(confirmSignUpRequest.getConfirmationCode())
                    .build();
            cognitoService.confirmSignUp(cognitoConfirmSignUpRequest);
            log.info("[Auth][ConfirmSignUp]: Success");
        } catch (Exception e) {
            log.error("[Auth][ConfirmSignUp]: Error {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public TokenResponse refreshToken(String token) {
        try {
            log.info("[Auth][RefreshToken]: Start {}", token);

            if (token == null) {
                throw new BadRequestException(
                        ErrorMessageCode.BAD_REQUEST,
                        mr.resolve(ErrorMessageCode.BAD_REQUEST),
                        new RuntimeException("Refresh token is null")
                );
            }
            log.info("[Auth][RefreshToken]: Success");
            AuthenticationResultType authRes = cognitoService.refreshToken(token);
            return buildTokenResponse(authRes);
        } catch (Exception e) {
            log.error("[Auth][RefreshToken]: Error {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void resendCode(ResendCodeRequest resendCodeRequest) {
        try {
            log.info("[Auth][ResendCode]: Start {}", resendCodeRequest.getUsername());
            cognitoService.resendEmail(resendCodeRequest.getUsername());
            log.info("[Auth][ResendCode]: Success");
        } catch (Exception e) {
            log.error("[Auth][ResendCode]: Error {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void changePassword(ChangePasswordRequestDto request) {
        try {
            log.info("[Auth][changePassword]: Start");
            cognitoService.modifyPassword(request);
            log.info("[Auth][changePassword]: Success");
        } catch (Exception e) {
            log.error("[Auth][changePassword]: Error {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


}

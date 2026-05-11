package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.dto.request.ChangePasswordRequestDto;
import com.nhantic.trelloapi.dto.request.CognitoConfirmSignUpRequest;
import com.nhantic.trelloapi.dto.request.CognitoSignInRequest;
import com.nhantic.trelloapi.dto.request.CognitoSignUpRequest;
import com.nhantic.trelloapi.exception.BadRequestException;
import com.nhantic.trelloapi.exception.ConflictException;
import com.nhantic.trelloapi.exception.InternalServerErrorException;
import com.nhantic.trelloapi.exception.UnauthorizedException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.ICognitoService;
import com.nhantic.trelloapi.util.CognitoSecretHash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CognitoServiceImpl implements ICognitoService {
    @Value("${COGNITO_CLIENT_ID}")
    private String CLIENT_ID;

    @Value("${COGNITO_CLIENT_SECRET}")
    private String CLIENT_SECRET;

    private final CognitoIdentityProviderClient cognitoClient;
    private final MessageResolver mr;

    @Override
    public AuthenticationResultType signIn(CognitoSignInRequest dto) {
        try {
            log.info("[Cognito][signIn]: {}", dto.getUsername());
            String secretHash =
                    CognitoSecretHash.generateSecretHash(
                            dto.getUsername(),
                            CLIENT_ID,
                            CLIENT_SECRET
                    );

            Map<String, String> params = new HashMap<>();
            params.put("USERNAME", dto.getUsername());
            params.put("PASSWORD", dto.getPassword());
            params.put("SECRET_HASH", secretHash);

            InitiateAuthRequest req = InitiateAuthRequest.builder()
                    .clientId(CLIENT_ID)
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .authParameters(params)
                    .build();
            return cognitoClient.initiateAuth(req).authenticationResult();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof UserNotConfirmedException) {
                throw new UnauthorizedException(
                        ErrorMessageCode.USER_NOT_CONFIRMED,
                        mr.resolve(ErrorMessageCode.USER_NOT_CONFIRMED)
                );
            }

            if (e instanceof NotAuthorizedException) {
                throw new UnauthorizedException(
                        ErrorMessageCode.INVALID_CREDENTIALS,
                        mr.resolve(ErrorMessageCode.INVALID_CREDENTIALS)
                );
            }
            throw new InternalServerErrorException(ErrorMessageCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @Override
    public void signUp(CognitoSignUpRequest dto) {
        try {
            log.info("[Cognito][signUp]: {}", dto.getUsername());
            String secretHash =  CognitoSecretHash.generateSecretHash(
                    dto.getUsername(),
                    CLIENT_ID,
                    CLIENT_SECRET
            );

            SignUpRequest req = SignUpRequest.builder()
                    .clientId(CLIENT_ID)
                    .username(dto.getUsername())
                    .password(dto.getPassword())
                    .userAttributes(
                            AttributeType.builder().name("custom:fullName").value(dto.getFullName()).build()
                    )
                    .secretHash(secretHash)
                    .build();
            cognitoClient.signUp(req);
        } catch (UsernameExistsException e) {
            throw new ConflictException(ErrorMessageCode.USER_EXISTS, mr.resolve(ErrorMessageCode.USER_EXISTS));
        } catch (Exception e) {
            throw new InternalServerErrorException(ErrorMessageCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @Override
    public void confirmSignUp(CognitoConfirmSignUpRequest dto) {
        try {
            log.info("[Cognito][confirmSignUp]: {}", dto.getUsername());
            String secretHash =  CognitoSecretHash.generateSecretHash(
                    dto.getUsername(),
                    CLIENT_ID,
                    CLIENT_SECRET
            );
            ConfirmSignUpRequest req = ConfirmSignUpRequest.builder()
                    .clientId(CLIENT_ID)
                    .username(dto.getUsername())
                    .confirmationCode(dto.getConfirmationCode())
                    .secretHash(secretHash)
                    .build();
            cognitoClient.confirmSignUp(req);
        } catch (CodeMismatchException e) {
            throw new BadRequestException(ErrorMessageCode.INVALID_VERIFICATION_CODE, mr.resolve(ErrorMessageCode.INVALID_VERIFICATION_CODE));
        }
        catch (Exception e) {
            throw new InternalServerErrorException(ErrorMessageCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public AuthenticationResultType refreshToken(String token) {
        try {
            log.info("[Cognito][refreshToken]: {}", token);

            Map<String, String> authParams = new HashMap<>();
            authParams.put("REFRESH_TOKEN", token);

            InitiateAuthRequest request = InitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                    .clientId(CLIENT_ID) // app client id
                    .authParameters(authParams)
                    .build();

            InitiateAuthResponse response = cognitoClient.initiateAuth(request);

            return response.authenticationResult();
        } catch (Exception e) {
            throw new InternalServerErrorException(ErrorMessageCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void resendEmail(String username) {
        try {
            log.info("[Cognito][resendEmail]: Start {}", username);
            String secretHash =  CognitoSecretHash.generateSecretHash(
                    username,
                    CLIENT_ID,
                    CLIENT_SECRET
            );
            ResendConfirmationCodeRequest request = ResendConfirmationCodeRequest.builder()
                    .clientId(CLIENT_ID)
                    .username(username)
                    .secretHash(secretHash)
                    .build();
            ResendConfirmationCodeResponse response = cognitoClient.resendConfirmationCode(request);
            log.info("[Cognito][resendEmail]: Success {}", response);
        } catch (Exception e) {
            throw new InternalServerErrorException(ErrorMessageCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void modifyPassword(ChangePasswordRequestDto request) {
        try {
            ChangePasswordRequest req = ChangePasswordRequest.builder()
                    .accessToken(request.getAccessToken())
                    .previousPassword(request.getOldPassword())
                    .proposedPassword(request.getNewPassword())
                    .build();
            ChangePasswordResponse res = cognitoClient.changePassword(req);
            log.info("[Cognito][modifyPassword]: Success {}", res);
        } catch (Exception e) {
            throw new InternalServerErrorException(ErrorMessageCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

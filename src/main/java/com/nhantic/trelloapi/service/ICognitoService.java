package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.request.ChangePasswordRequest;
import com.nhantic.trelloapi.dto.request.CognitoConfirmSignUpRequest;
import com.nhantic.trelloapi.dto.request.CognitoSignInRequest;
import com.nhantic.trelloapi.dto.request.CognitoSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

public interface ICognitoService {
    AuthenticationResultType signIn(CognitoSignInRequest dto);
    void signUp(CognitoSignUpRequest dto);
    void confirmSignUp(CognitoConfirmSignUpRequest dto);
    AuthenticationResultType refreshToken(String token);
    void resendEmail(String username);
    void modifyPassword(ChangePasswordRequest request);
}

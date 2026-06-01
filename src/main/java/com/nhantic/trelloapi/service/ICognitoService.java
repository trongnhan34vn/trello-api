package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.request.*;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

public interface ICognitoService {
    AuthenticationResultType signIn(CognitoSignInRequest dto);
    void signUp(CognitoSignUpRequest dto);
    void confirmSignUp(CognitoConfirmSignUpRequest dto);
    AuthenticationResultType refreshToken(CognitoRefreshTokenRequest req);
    void resendEmail(String username);
    void modifyPassword(ChangePasswordRequest request);
}

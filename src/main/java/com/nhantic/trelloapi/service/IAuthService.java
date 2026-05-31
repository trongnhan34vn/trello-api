package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.request.*;
import com.nhantic.trelloapi.dto.response.TokenResponse;

public interface IAuthService {
     TokenResponse signIn(SignInRequest signInRequest);
     void signUp(SignUpRequest signUpRequest);
     void confirmSignUp(ConfirmSignUpRequest confirmSignUpRequest);
     TokenResponse refreshToken(RefreshTokenRequest request);
     void signOut();
     void resendCode(ResendCodeRequest resendCodeRequest);
     void changePassword(ChangePasswordRequest request);
}

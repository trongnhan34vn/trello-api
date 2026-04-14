package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.request.ConfirmSignUpRequest;
import com.nhantic.trelloapi.dto.request.ResendCodeRequest;
import com.nhantic.trelloapi.dto.request.SignInRequest;
import com.nhantic.trelloapi.dto.request.SignUpRequest;
import com.nhantic.trelloapi.dto.response.TokenResponse;

public interface IAuthService {
     TokenResponse signIn(SignInRequest signInRequest);
     void signUp(SignUpRequest signUpRequest);
     void confirmSignUp(ConfirmSignUpRequest confirmSignUpRequest);
     TokenResponse refreshToken(String token);
     void resendCode(ResendCodeRequest resendCodeRequest);
}

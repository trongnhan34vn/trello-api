package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.UserInternalResponse;
import com.nhantic.trelloapi.dto.response.UserResponse;
import org.springframework.security.oauth2.jwt.Jwt;

public interface IUserQueryService {
    UserInternalResponse findByCognitoId(String cognitoId);
    boolean existsByCognitoId(String cognitoId);
    UserResponse getMe(Jwt jwt);
}

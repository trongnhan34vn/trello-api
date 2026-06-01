package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.entity.UserSession;

import java.util.UUID;

public interface IUserSessionService {
    void save(UUID userId, String cognitoId, String refreshToken);
    UserSession findByRefreshToken(String refreshToken);
    void deleteByRefreshToken(String refreshToken);
}

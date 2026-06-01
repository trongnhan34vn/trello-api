package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.entity.UserSession;
import com.nhantic.trelloapi.exception.UnauthorizedException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.repository.IUserSessionRepository;
import com.nhantic.trelloapi.service.IUserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl implements IUserSessionService {
    private final IUserSessionRepository userSessionRepository;
    private final MessageResolver mr;

    @Override
    @Transactional
    public void save(UUID userId, String cognitoId, String refreshToken) {
        log.info("[UserSession][save]: userId={}", userId);
        UserSession session = UserSession.builder()
                .userId(userId)
                .cognitoId(cognitoId)
                .refreshToken(refreshToken)
                .expiredAt(LocalDateTime.now().plusDays(30))
                .build();
        userSessionRepository.save(session);
    }

    @Override
    public UserSession findByRefreshToken(String refreshToken) {
        log.info("[UserSession][findByRefreshToken] Executing");
        return userSessionRepository.findByRefreshToken(refreshToken)
                .filter(session -> session.getExpiredAt().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new UnauthorizedException(
                        ErrorMessageCode.UNAUTHORIZED,
                        mr.resolve(ErrorMessageCode.UNAUTHORIZED)
                ));
    }

    @Override
    @Transactional
    public void deleteByRefreshToken(String refreshToken) {
        log.info("[UserSession][deleteByRefreshToken] Executing");
        userSessionRepository.deleteByRefreshToken(refreshToken);
    }
}

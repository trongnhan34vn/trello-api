package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.dto.request.UserCreateRequest;
import com.nhantic.trelloapi.dto.response.UserCreateResponse;
import com.nhantic.trelloapi.dto.response.UserInternalResponse;
import com.nhantic.trelloapi.dto.response.UserResponse;
import com.nhantic.trelloapi.entity.User;
import com.nhantic.trelloapi.exception.NotFoundException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.repository.IUserRepository;
import com.nhantic.trelloapi.service.IUserCommandService;
import com.nhantic.trelloapi.service.IUserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nhantic.trelloapi.dto.request.UpdateProfileRequest;
import com.nhantic.trelloapi.exception.ForbiddenException;
import java.util.UUID;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserCommandService, IUserQueryService {
    private final IUserRepository userRepository;
    private final MessageResolver mr;

    @Override
    public UserCreateResponse create(UserCreateRequest dto) {
        try {
            log.info("[User][create] Start");
            User user = User.builder()
                    .cognitoId(dto.getCognitoId())
                    .email(dto.getEmail())
                    .fullName(dto.getFullName())
                    .build();
            User savedUser = userRepository.save(user);
            log.info("[User][create] Success");
            return UserCreateResponse.builder()
                    .fullName(savedUser.getFullName())
                    .email(savedUser.getEmail())
                    .avatarUrl(savedUser.getAvatarUrl())
                    .build();
        } catch (Exception e) {
            log.error("[User][create] Error {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public UserInternalResponse findByCognitoId(String cognitoId) {
        try {
            log.info("[User][findByCognitoId] Start");
            User user = userRepository.findByCognitoId(cognitoId).orElseThrow(() -> new NotFoundException(ErrorMessageCode.USER_NOT_FOUND, mr.resolve(ErrorMessageCode.USER_NOT_FOUND)));
            log.info("[User][findByCognitoId] Success");
            return UserInternalResponse.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .avatarUrl(user.getAvatarUrl())
                    .cognitoId(cognitoId)
                    .createdAt(user.getCreatedAt().toString())
                    .updatedAt(user.getUpdatedAt().toString())
                    .build();
        } catch (Exception e) {
            log.error("[User][findByCognitoId] Error {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public UserInternalResponse findByCognitoIdOrNull(String cognitoId) {
        return userRepository.findByCognitoId(cognitoId).map(user -> UserInternalResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .cognitoId(cognitoId)
                .createdAt(user.getCreatedAt().toString())
                .updatedAt(user.getUpdatedAt().toString())
                .build()).orElse(null);
    }

    @Override
    public boolean existsByCognitoId(String cognitoId) {
        return userRepository.existsByCognitoId(cognitoId);
    }

    @Override
    public UserResponse getMe(Jwt jwt) {
        try {
            log.info("[User][getMe] Start");
            String cognitoId = jwt.getSubject();
            User user = userRepository.findByCognitoId(cognitoId).orElseThrow(
                    () -> new NotFoundException(ErrorMessageCode.USER_NOT_FOUND, mr.resolve(ErrorMessageCode.USER_NOT_FOUND))
            );
            log.info("[User][getMe] Success");
            return UserResponse.builder()
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .avatarUrl(user.getAvatarUrl())
                    .bio(user.getBio())
                    .phone(user.getPhone())
                    .address(user.getAddress())
                    .id(user.getId().toString())
                    .createdAt(user.getCreatedAt().toString())
                    .updatedAt(user.getUpdatedAt().toString())
                    .build();
        } catch (Exception e) {
            log.error("[User][getMe] Error {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public List<UserResponse> findAll(String search, String current) {
        try {
            log.info("[User][findAll]: Start");
            List<User> users = userRepository.searchAll(search);
            users = users.stream().filter(u -> !u.getCognitoId().equalsIgnoreCase(current)).toList();
            log.info("[User][findAll]: Success {} items", users.size());
            return users.stream().map((u) -> (UserResponse.builder()
                    .id(u.getId().toString())
                    .email(u.getEmail())
                    .fullName(u.getFullName())
                    .avatarUrl(u.getAvatarUrl())
                    .bio(u.getBio())
                    .phone(u.getPhone())
                    .address(u.getAddress())
                    .build())).toList();
        } catch (Exception e) {
            log.info("[User][findAll]: Error {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        try {
            log.info("[User][updateProfile] Start");
            String cognitoId = request.getCognitoId();
            User user = userRepository.findByCognitoId(cognitoId).orElseThrow(
                    () -> new NotFoundException(ErrorMessageCode.USER_NOT_FOUND, mr.resolve(ErrorMessageCode.USER_NOT_FOUND))
            );
            if (request.getFullName() != null) {
                user.setFullName(request.getFullName());
            }
            if (request.getAvatarUrl() != null) {
                user.setAvatarUrl(request.getAvatarUrl());
            }
            user.setBio(request.getBio());
            user.setPhone(request.getPhone());
            user.setAddress(request.getAddress());
            
            User savedUser = userRepository.save(user);
            log.info("[User][updateProfile] Success");
            return UserResponse.builder()
                    .fullName(savedUser.getFullName())
                    .email(savedUser.getEmail())
                    .avatarUrl(savedUser.getAvatarUrl())
                    .bio(savedUser.getBio())
                    .phone(savedUser.getPhone())
                    .address(savedUser.getAddress())
                    .id(savedUser.getId().toString())
                    .createdAt(savedUser.getCreatedAt().toString())
                    .updatedAt(savedUser.getUpdatedAt().toString())
                    .build();
        } catch (Exception e) {
            log.error("[User][updateProfile] Error {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}

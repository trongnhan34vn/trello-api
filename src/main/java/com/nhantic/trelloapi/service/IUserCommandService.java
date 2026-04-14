package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.request.UserCreateRequest;
import com.nhantic.trelloapi.dto.response.UserCreateResponse;

public interface IUserCommandService {
    UserCreateResponse create(UserCreateRequest dto);
}

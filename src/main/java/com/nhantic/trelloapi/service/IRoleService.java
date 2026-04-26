package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.RoleResponse;

import java.util.List;

public interface IRoleService {
    List<RoleResponse> findAll();
}

package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.dto.response.RoleResponse;
import com.nhantic.trelloapi.entity.Role;
import com.nhantic.trelloapi.repository.IRoleRepository;
import com.nhantic.trelloapi.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {
    private final IRoleRepository roleRepository;
    @Override
    public List<RoleResponse> findAll() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(r -> RoleResponse.builder().id(r.getId()).name(r.getName().toString()).build()).toList();
    }
}

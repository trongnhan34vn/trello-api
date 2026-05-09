package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.dto.request.WorkspaceMemberCreateRequest;
import com.nhantic.trelloapi.dto.response.UserResponse;
import com.nhantic.trelloapi.dto.response.WorkspaceMemberCreateResponse;
import com.nhantic.trelloapi.dto.response.WorkspaceMemberResponse;
import com.nhantic.trelloapi.entity.Role;
import com.nhantic.trelloapi.entity.User;
import com.nhantic.trelloapi.entity.Workspace;
import com.nhantic.trelloapi.entity.WorkspaceMember;
import com.nhantic.trelloapi.exception.NotFoundException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.repository.IRoleRepository;
import com.nhantic.trelloapi.repository.IUserRepository;
import com.nhantic.trelloapi.repository.IWorkspaceMemberRepository;
import com.nhantic.trelloapi.repository.IWorkspaceRepository;
import com.nhantic.trelloapi.service.IWorkspaceMemberCommandService;
import com.nhantic.trelloapi.service.IWorkspaceMemberQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceMemberServiceImpl implements IWorkspaceMemberQueryService, IWorkspaceMemberCommandService {
    private final IWorkspaceMemberRepository workspaceMemberRepository;
    private final IUserRepository userRepository;
    private final IWorkspaceRepository workspaceRepository;
    private final MessageResolver mr;
    private final IRoleRepository roleRepository;

    @Override
    public List<WorkspaceMemberResponse> findByWorkspaceId(String workspaceId) {
        try {
            log.info("[WorkspaceMember][findByWorkspaceId] Start: {}", workspaceId);
            List<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findByWorkspaceId(UUID.fromString(workspaceId));
            log.info("[WorkspaceMember][findByWorkspaceId] Found {} members ", workspaceMembers.size());
            return workspaceMembers.stream().map(wm -> {
                User user = wm.getUser();
                UserResponse userResponse = UserResponse.builder()
                        .id(user.getId().toString())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .avatarUrl(user.getAvatarUrl())
                        .updatedAt(user.getUpdatedAt().toString())
                        .createdAt(user.getCreatedAt().toString())
                        .build();
                return WorkspaceMemberResponse.builder()
                        .id(wm.getId().toString())
                        .build();
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("[WorkspaceMember][findByWorkspaceId] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<WorkspaceMemberCreateResponse> create(WorkspaceMemberCreateRequest request) {
        try {
            log.info("[WorkspaceMember][create] Start: req=[{}]", request);
            List<UUID> uuids = request.getUserIds().stream().map(UUID::fromString).toList();
            List<User> users = userRepository.findUsersByIdIn(uuids);
            Workspace workspace = workspaceRepository.findById(UUID.fromString(request.getWorkspaceId())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.WORKSPACE_NOT_FOUND, mr.resolve(ErrorMessageCode.WORKSPACE_NOT_FOUND)));
            Role role = roleRepository.findById(request.getRoleId()).orElseThrow(() -> new NotFoundException(ErrorMessageCode.ROLE_NOT_FOUND, mr.resolve(ErrorMessageCode.ROLE_NOT_FOUND)));
            List<WorkspaceMember> preCreatedWorkspaceMembers = new ArrayList<>();

            for (User user : users) {
                WorkspaceMember preCreatedWorkspaceMember = WorkspaceMember.builder()
                        .user(user)
                        .workspace(workspace)
                        .role(role)
                        .createdBy(UUID.fromString(request.getCreatedBy()))
                        .build();
                preCreatedWorkspaceMembers.add(preCreatedWorkspaceMember);
            }

            List<WorkspaceMember> workspaceMembers = workspaceMemberRepository.saveAll(preCreatedWorkspaceMembers);
            log.info("[WorkspaceMember][create] Success: created {} items", workspaceMembers.size());

            return workspaceMembers.stream().map(wm -> WorkspaceMemberCreateResponse.builder()
                    .userId(wm.getUser().getId().toString())
                    .id(wm.getId().toString())
                    .workspaceId(wm.getWorkspace().getId().toString())
                    .roleId(wm.getRole().getId())
                    .email(wm.getUser().getEmail())
                    .fullName(wm.getUser().getFullName())
                    .avatarUrl(wm.getUser().getAvatarUrl())
                    .build()).toList();

        } catch (Exception e) {
            log.error("[WorkspaceMember][create] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}

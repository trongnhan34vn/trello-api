package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.dto.response.UserResponse;
import com.nhantic.trelloapi.dto.response.WorkspaceMemberResponse;
import com.nhantic.trelloapi.entity.User;
import com.nhantic.trelloapi.entity.WorkspaceMember;
import com.nhantic.trelloapi.repository.IWorkspaceMemberRepository;
import com.nhantic.trelloapi.service.IWorkspaceMemberQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceMemberServiceImpl implements IWorkspaceMemberQueryService {
    private final IWorkspaceMemberRepository workspaceMemberRepository;
    @Override
    public List<WorkspaceMemberResponse> listMembersByWorkspaceId(String workspaceId) {
        try {
            log.info("[WorkspaceMember][listByWorkspaceId] Start: {}", workspaceId);
            List<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findByWorkspaceId(UUID.fromString(workspaceId));
            log.info("[WorkspaceMember][listByWorkspaceId] Found {} members ", workspaceMembers.size());
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
            log.error("[WorkspaceMember][listByWorkspaceId] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}

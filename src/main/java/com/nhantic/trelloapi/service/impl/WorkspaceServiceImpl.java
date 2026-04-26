package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.constant.RoleName;
import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.request.WorkspaceCreateRequest;
import com.nhantic.trelloapi.dto.response.BoardResponse;
import com.nhantic.trelloapi.dto.response.WorkspaceCategoryResponse;
import com.nhantic.trelloapi.dto.response.WorkspaceCreateResponse;
import com.nhantic.trelloapi.dto.response.WorkspaceResponse;
import com.nhantic.trelloapi.entity.*;
import com.nhantic.trelloapi.exception.NotFoundException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.repository.*;
import com.nhantic.trelloapi.repository.dto.WorkspaceWithBoard;
import com.nhantic.trelloapi.service.IUserQueryService;
import com.nhantic.trelloapi.service.IWorkspaceCategoryQueryService;
import com.nhantic.trelloapi.service.IWorkspaceCommandService;
import com.nhantic.trelloapi.service.IWorkspaceQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements IWorkspaceQueryService, IWorkspaceCommandService {
    private final IWorkspaceRepository workspaceRepository;
    private final MessageResolver mr;
    private final IWorkspaceCategoryRepository workspaceCategoryRepository;
    private final IWorkspaceMemberRepository workspaceMemberRepository;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;

    @Override
    public List<WorkspaceResponse> searchByCognitoId(String cognitoId, String search) {
        try {
            log.info("[Workspace][search] Start: cognito=[{}] search=[{}]", cognitoId, search);
            List<WorkspaceWithBoard> workspaceWithBoards = workspaceRepository.searchByUserId(cognitoId, search);
            log.info("[Workspace][search] Items: {}", workspaceWithBoards.size());
            List<WorkspaceResponse> ws = getWorkspaceResponses(workspaceWithBoards);
            log.info("[Workspace][search] Found: {} workspaces", ws.size());

            return ws;
        } catch (Exception e) {
            log.error("[Workspace][search] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private static List<WorkspaceResponse> getWorkspaceResponses(List<WorkspaceWithBoard> workspaceWithBoards) {
        Map<UUID, WorkspaceResponse> workspaceResponseMap = new HashMap<>();
        for (WorkspaceWithBoard item : workspaceWithBoards) {
            WorkspaceResponse workspace = workspaceResponseMap.computeIfAbsent(item.getWorkspaceId(), id ->
                    WorkspaceResponse.builder()
                            .name(item.getWorkspaceName())
                            .id(id.toString())
                            .build()
            );

            if (item.getBoardId() != null) {
                BoardResponse board = BoardResponse.builder()
                        .id(item.getBoardId().toString())
                        .name(item.getBoardName())
                        .backgroundUrl(item.getBackgroundUrl())
                        .build();
                workspace.getBoards().add(board);
            }
        }
        return new ArrayList<>(workspaceResponseMap.values());
    }


    @Override
    public WorkspaceResponse findById(String id) {
        try {
            log.info("[Workspace][findById] Start: {}", id);
            Workspace workspace = workspaceRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException(ErrorMessageCode.WORKSPACE_NOT_FOUND, mr.resolve(ErrorMessageCode.WORKSPACE_NOT_FOUND)));
            log.info("[Workspace][findById] Found: {}", workspace.getName());
            return WorkspaceResponse.builder()
                    .id(workspace.getId().toString())
                    .name(workspace.getName())
                    .description(workspace.getDescription())
                    .build();
        } catch (Exception e) {
            log.error("[Workspace][findById] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public WorkspaceResponse searchByCognitoIdAndWorkspaceId(String cognitoId, String workspaceId) {
        try {
            log.info("[Workspace][searchByCognitoIdAndWorkspaceId] Start: {}", workspaceId);
            List<WorkspaceWithBoard> workspaceWithBoards = workspaceRepository.searchByUserIdAndWorkspaceId(cognitoId, workspaceId);
            List<WorkspaceResponse> workspaceResponses = getWorkspaceResponses(workspaceWithBoards);
            log.info("[Workspace][searchByCognitoIdAndWorkspaceId] Found: {}", workspaceResponses.size());
            return workspaceResponses.getFirst();
        } catch (Exception e) {
            log.error("[Workspace][searchByCognitoIdAndWorkspaceId] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public WorkspaceCreateResponse create(WorkspaceCreateRequest req) {
        try {
            log.info("[Workspace][create] Start: " + req.getName());
            // create workspaces
            WorkspaceCategory wc = workspaceCategoryRepository.findById(req.getCategoryId()).orElseThrow(() -> new NotFoundException(ErrorMessageCode.WORKSPACE_CATEGORY_NOT_FOUND, mr.resolve(ErrorMessageCode.WORKSPACE_CATEGORY_NOT_FOUND)));
            Workspace ws = Workspace.builder()
                    .name(req.getName())
                    .description(req.getDescription())
                    .category(wc)
                    .createdBy(UUID.fromString(req.getCreatedBy()))
                    .build();
            Workspace workspace = workspaceRepository.save(ws);

            // create member
            User user = userRepository.findById(UUID.fromString(req.getCreatedBy())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.USER_NOT_FOUND, mr.resolve(ErrorMessageCode.USER_NOT_FOUND)));
            Role role = roleRepository.findByName(RoleName.ADMIN).orElseThrow(() -> new NotFoundException(ErrorMessageCode.ROLE_NOT_FOUND, mr.resolve(ErrorMessageCode.ROLE_NOT_FOUND)));
            WorkspaceMember preCreateWM = WorkspaceMember.builder()
                    .user(user)
                    .workspace(workspace)
                    .role(role)
                    .build();
            workspaceMemberRepository.save(preCreateWM);

            log.info("[Workspace][create] created: " + workspace.getName());
            return WorkspaceCreateResponse.builder()
                    .id(workspace.getId().toString())
                    .name(workspace.getName())
                    .description(workspace.getDescription())
                    .createdBy(workspace.getCreatedBy().toString())
                    .build();
        } catch (Exception e) {
            log.error("[Workspace][create] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}

package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.constant.RoleName;
import com.nhantic.trelloapi.dto.request.WorkspaceCreateRequest;
import com.nhantic.trelloapi.dto.response.BoardResponse;
import com.nhantic.trelloapi.dto.response.WorkspaceCreateResponse;
import com.nhantic.trelloapi.dto.response.WorkspaceMemberResponse;
import com.nhantic.trelloapi.dto.response.WorkspaceResponse;
import com.nhantic.trelloapi.entity.*;
import com.nhantic.trelloapi.exception.NotFoundException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.repository.*;
import com.nhantic.trelloapi.repository.dto.WorkspaceRecord;
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
            List<WorkspaceRecord> records = workspaceRepository.searchByUserId(cognitoId, search);
            log.info("[Workspace][search] Items: {}", records.size());
            List<WorkspaceResponse> ws = mapWorkspaceResponses(records);
            log.info("[Workspace][search] Found: {} workspaces", ws.size());

            return ws;
        } catch (Exception e) {
            log.error("[Workspace][search] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private static List<WorkspaceResponse> mapWorkspaceResponses(List<WorkspaceRecord> workspaceRecordWithBoards) {
        Map<UUID, WorkspaceResponse> workspaceResponseMap = new HashMap<>();
        for (WorkspaceRecord item : workspaceRecordWithBoards) {
            WorkspaceResponse workspace = workspaceResponseMap.computeIfAbsent(item.getWorkspaceId(), id ->
                    WorkspaceResponse.builder()
                            .name(item.getWorkspaceName())
                            .id(id.toString())
                            .boards(new ArrayList<>())
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
    public WorkspaceResponse searchByWorkspaceId(String workspaceId, String cognitoId) {
        try {
            log.info("[Workspace][searchByWorkspaceId] Start: {}", workspaceId);
            List<WorkspaceRecord> records = workspaceRepository.searchByWorkspaceId(workspaceId, cognitoId);
            log.info("[Workspace][searchByWorkspaceId] records: {}", records.size());
            return mapToWorkspaceResponse(records);
        } catch (Exception e) {
            log.error("[Workspace][searchByWorkspaceId] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private WorkspaceResponse mapToWorkspaceResponse(List<WorkspaceRecord> records) {
        WorkspaceResponse workspace = null;
        List<BoardResponse> boards = new ArrayList<>();
        List<WorkspaceMemberResponse> members = new ArrayList<>();

        for (WorkspaceRecord row : records) {
            if (workspace == null) {
                workspace = mapWorkspace(row);
            }
            if ("BOARD".equals(row.getRowType()) && row.getBoardId() != null) {
                boards.add(mapBoard(row));
            } else if ("MEMBER".equals(row.getRowType())) {
                members.add(mapMember(row));
            }
        }

        if (workspace != null) {
            workspace.setBoards(boards);
            workspace.setMembers(members);
        }

        return workspace;
    }

    private WorkspaceResponse mapWorkspace(WorkspaceRecord row) {
        return WorkspaceResponse.builder()
                .id(row.getWorkspaceId().toString())
                .name(row.getWorkspaceName())
                .description(row.getDescription())
                .build();
    }

    private BoardResponse mapBoard(WorkspaceRecord row) {
        return BoardResponse.builder()
                .id(row.getBoardId().toString())
                .name(row.getBoardName())
                .backgroundUrl(row.getBackgroundUrl())
                .build();
    }

    private WorkspaceMemberResponse mapMember(WorkspaceRecord row) {
        return WorkspaceMemberResponse.builder()
                .id(row.getMemberId().toString())
                .fullName(row.getMemberFullName())
                .email(row.getMemberEmail())
                .avatarUrl(row.getMemberAvatar())
                .roleId(row.getMemberRoleId())
                .build();
    }

    @Override
    @Transactional
    public WorkspaceCreateResponse create(WorkspaceCreateRequest req) {
        try {
            log.info("[Workspace][create] Start: {}", req.getName());
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

            log.info("[Workspace][create] created: {}", workspace.getName());
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

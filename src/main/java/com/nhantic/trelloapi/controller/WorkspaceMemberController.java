package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.request.WorkspaceMemberCreateRequest;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.dto.response.WorkspaceMemberCreateResponse;
import com.nhantic.trelloapi.dto.response.WorkspaceMemberResponse;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IWorkspaceMemberCommandService;
import com.nhantic.trelloapi.service.IWorkspaceMemberQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspace-members")
@RequiredArgsConstructor
public class WorkspaceMemberController {
    private final IWorkspaceMemberQueryService workspaceMemberQueryService;
    private final IWorkspaceMemberCommandService workspaceMemberCommandService;
    private final MessageResolver mr;
    @GetMapping()
    public ResponseEntity<?> get(@RequestParam("workspaceId") String workspaceId) {
        List<WorkspaceMemberResponse> members = workspaceMemberQueryService.findByWorkspaceId(workspaceId);
        Response response = Response.builder()
                .success(true)
                .code(SuccessMessageCode.WORKSPACE_MEMBER_FOUND)
                .message(mr.resolve(SuccessMessageCode.WORKSPACE_MEMBER_FOUND))
                .data(members)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody @Valid WorkspaceMemberCreateRequest request) {
        List<WorkspaceMemberCreateResponse> members = workspaceMemberCommandService.create(request);
        Response response = Response.builder()
                .success(true)
                .code(SuccessMessageCode.WORKSPACE_MEMBER_CREATED_SUCCESS)
                .message(mr.resolve(SuccessMessageCode.WORKSPACE_MEMBER_CREATED_SUCCESS))
                .data(members)
                .build();
        return ResponseEntity.ok(response);
    }

}

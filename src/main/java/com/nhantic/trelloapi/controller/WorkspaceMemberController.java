package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.dto.response.WorkspaceMemberResponse;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IWorkspaceMemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspace-members")
@RequiredArgsConstructor
public class WorkspaceMemberController {
    private final IWorkspaceMemberQueryService workspaceMemberQueryService;
    private final MessageResolver mr;
    @GetMapping()
    public ResponseEntity<?> listMembersByWorkspaceId(@RequestParam("workspaceId") String workspaceId) {
        List<WorkspaceMemberResponse> members = workspaceMemberQueryService.listMembersByWorkspaceId(workspaceId);
        Response response = Response.builder()
                .success(true)
                .code(SuccessMessageCode.WORKSPACE_MEMBER_FOUND)
                .message(mr.resolve(SuccessMessageCode.WORKSPACE_MEMBER_FOUND))
                .data(members)
                .build();
        return ResponseEntity.ok(response);
    }
}

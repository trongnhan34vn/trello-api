package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.request.WorkspaceMemberCreateRequest;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.dto.response.WorkspaceMemberCreateResponse;
import com.nhantic.trelloapi.dto.response.WorkspaceMemberResponse;
import com.nhantic.trelloapi.helper.BuildCreatedByFromJwt;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IUserQueryService;
import com.nhantic.trelloapi.service.IWorkspaceMemberCommandService;
import com.nhantic.trelloapi.service.IWorkspaceMemberQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspace-members")
@RequiredArgsConstructor
@Tag(name = "Workspace Member", description = "Endpoints for managing workspace members")
public class WorkspaceMemberController {
    private final IWorkspaceMemberQueryService workspaceMemberQueryService;
    private final IWorkspaceMemberCommandService workspaceMemberCommandService;
    private final IUserQueryService userQueryService;
    private final MessageResolver mr;

    @Operation(
            summary = "Get workspace members",
            description = "Retrieve all members of a specific workspace",
            parameters = {
                    @Parameter(
                            name = "workspaceId",
                            description = "Workspace ID",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Members retrieved successfully",
                            content = @Content(
                                    schema = @Schema(implementation = Response.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema())
                    )
            }
    )
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

    @Operation(
            summary = "Add members to workspace",
            description = "Add one or more members to a workspace",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Workspace member creation payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = WorkspaceMemberCreateRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Members added successfully",
                            content = @Content(
                                    schema = @Schema(implementation = Response.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(schema = @Schema())
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PostMapping()
    public ResponseEntity<?> create(@RequestBody @Valid WorkspaceMemberCreateRequest request, @AuthenticationPrincipal Jwt jwt) {
        String createdBy = BuildCreatedByFromJwt.execute(userQueryService, mr, jwt);
        request.setCreatedBy(createdBy);
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

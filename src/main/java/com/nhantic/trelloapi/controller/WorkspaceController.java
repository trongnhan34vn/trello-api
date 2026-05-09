package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.request.WorkspaceCreateRequest;
import com.nhantic.trelloapi.dto.response.*;
import com.nhantic.trelloapi.exception.BadRequestException;
import com.nhantic.trelloapi.helper.BuildCreatedByFromJwt;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IUserQueryService;
import com.nhantic.trelloapi.service.IWorkspaceCommandService;
import com.nhantic.trelloapi.service.IWorkspaceQueryService;
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
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
@Tag(name = "Workspace", description = "Endpoints for workspace entity")
public class WorkspaceController {
    private final IWorkspaceQueryService workspaceQueryService;
    private final IWorkspaceCommandService workspaceCommandService;
    private final IUserQueryService userQueryService;
    private final MessageResolver mr;

    @Operation(
            summary = "Get workspaces",
            description = "Get all workspaces of current user with optional search keyword",
            parameters = {
                    @Parameter(
                            name = "search",
                            description = "Search keyword for workspace name"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Workspaces retrieved successfully",
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
    public ResponseEntity<?> list(@AuthenticationPrincipal Jwt jwt) {
        List<WorkspaceResponse> workspaces = workspaceQueryService.searchByCognitoId(jwt.getSubject());
        Response res = Response.builder()
                .success(true)
                .data(workspaces)
                .message(mr.resolve(SuccessMessageCode.WORKSPACE_FOUND))
                .code(SuccessMessageCode.WORKSPACE_FOUND)
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Create workspace",
            description = "Create a new workspace",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Workspace creation payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = WorkspaceCreateRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Workspace created successfully",
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
    public ResponseEntity<?> create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody WorkspaceCreateRequest req) {
        String createdBy = BuildCreatedByFromJwt.execute(userQueryService, mr, jwt);

        req.setCreatedBy(createdBy);
        WorkspaceCreateResponse ws = workspaceCommandService.create(req);
        Response res = Response.builder()
                .success(true)
                .data(ws)
                .message(mr.resolve(SuccessMessageCode.WORKSPACE_CREATED_SUCCESS))
                .code(SuccessMessageCode.WORKSPACE_CREATED_SUCCESS)
                .build();

        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Get workspace detail",
            description = "Retrieve detailed information of a workspace by ID",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Workspace ID",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Workspace retrieved successfully",
                            content = @Content(
                                    schema = @Schema(implementation = Response.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema())
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Workspace not found",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        WorkspaceResponse workspace = workspaceQueryService.searchByWorkspaceId(id, jwt.getSubject());
        Response res = Response.builder()
                .success(true)
                .data(workspace)
                .message(mr.resolve(SuccessMessageCode.WORKSPACE_FOUND))
                .code(SuccessMessageCode.WORKSPACE_FOUND)
                .build();
        return ResponseEntity.ok(res);
    }
}

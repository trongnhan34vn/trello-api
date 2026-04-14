package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.dto.response.WorkspaceCategoryResponse;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IWorkspaceCategoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspace-categories")
@RequiredArgsConstructor
@Tag(name = "WorkspaceCategory", description = "Endpoints for workspace category entity")
public class WorkspaceCategoryController {
    private final IWorkspaceCategoryQueryService workspaceCategoryQueryService;
    private final MessageResolver mr;

    @Operation(
            summary = "Get workspace categories",
            description = "Retrieve all available workspace categories",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Workspace categories retrieved successfully",
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
    public ResponseEntity<?> list() {
        List<WorkspaceCategoryResponse> wc = workspaceCategoryQueryService.findAll();
        Response res = Response.builder()
                .code(SuccessMessageCode.WORKSPACE_CATEGORY_FOUND)
                .message(mr.resolve(SuccessMessageCode.WORKSPACE_CATEGORY_FOUND))
                .data(wc)
                .success(true)
                .build();
        return ResponseEntity.ok(res);
    }
}

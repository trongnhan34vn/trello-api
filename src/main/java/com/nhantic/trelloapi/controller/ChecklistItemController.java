package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.request.ChecklistItemCreateRequest;
import com.nhantic.trelloapi.dto.request.ChecklistItemUpdateRequest;
import com.nhantic.trelloapi.dto.response.ChecklistItemCreateResponse;
import com.nhantic.trelloapi.dto.response.ChecklistItemUpdateResponse;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.helper.BuildCreatedByFromJwt;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IChecklistItemCommandService;
import com.nhantic.trelloapi.service.IUserQueryService;
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

@RestController
@RequestMapping("/api/v1/checklist-items")
@RequiredArgsConstructor
@Tag(name = "Checklist Item", description = "Endpoints for managing checklist items")
public class ChecklistItemController {
    private final IUserQueryService userQueryService;
    private final MessageResolver mr;
    private final IChecklistItemCommandService checklistItemCommandService;

    @Operation(
            summary = "Create checklist item",
            description = "Add a new item to a checklist",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Checklist item creation payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ChecklistItemCreateRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Checklist item created successfully",
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
    public ResponseEntity<?> create (@RequestBody @Valid ChecklistItemCreateRequest request, @AuthenticationPrincipal Jwt jwt) {
        String createdBy = BuildCreatedByFromJwt.execute(userQueryService, mr, jwt);
        request.setCreatedBy(createdBy);
        ChecklistItemCreateResponse checklist = checklistItemCommandService.create(request);
        Response res = Response.builder()
                .success(true)
                .code(SuccessMessageCode.CHECKLIST_ITEM_CREATED_SUCCESS)
                .message(mr.resolve(SuccessMessageCode.CHECKLIST_ITEM_CREATED_SUCCESS))
                .data(checklist)
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Update checklist item",
            description = "Update checklist item information by ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Checklist item update payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ChecklistItemUpdateRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Checklist item updated successfully",
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
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Checklist item not found",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody ChecklistItemUpdateRequest request, @AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        String updatedBy = BuildCreatedByFromJwt.execute(userQueryService, mr, jwt);
        request.setUpdatedBy(updatedBy);
        request.setId(id);

        ChecklistItemUpdateResponse checklist = checklistItemCommandService.update(request);

        Response res = Response.builder()
                .code(SuccessMessageCode.CHECKLIST_ITEM_UPDATED_SUCCESS)
                .message(mr.resolve(SuccessMessageCode.CHECKLIST_ITEM_UPDATED_SUCCESS))
                .success(true)
                .data(checklist)
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Delete checklist item",
            description = "Delete a checklist item by ID",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Checklist item ID",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Checklist item deleted successfully",
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
                            description = "Checklist item not found",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        checklistItemCommandService.delete(id);
        Response res = Response.builder()
                .code(SuccessMessageCode.CHECKLIST_ITEM_DELETED_SUCCESS)
                .message(mr.resolve(SuccessMessageCode.CHECKLIST_ITEM_DELETED_SUCCESS))
                .success(true)
                .data(null)
                .build();
        return ResponseEntity.ok(res);
    }
}

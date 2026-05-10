package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.request.ChecklistCreateRequest;
import com.nhantic.trelloapi.dto.request.ChecklistUpdateRequest;
import com.nhantic.trelloapi.dto.response.ChecklistCreateResponse;
import com.nhantic.trelloapi.dto.response.ChecklistResponse;
import com.nhantic.trelloapi.dto.response.ChecklistUpdateResponse;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.helper.BuildCreatedByFromJwt;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IChecklistCommandService;
import com.nhantic.trelloapi.service.IChecklistQueryService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/checklists")
@RequiredArgsConstructor
@Tag(name = "Checklist", description = "Endpoints for managing checklists")
public class ChecklistController {
    private final IChecklistQueryService checklistQueryService;
    private final IChecklistCommandService checklistCommandService;
    private final IUserQueryService userQueryService;
    private final MessageResolver mr;

    @Operation(
            summary = "Get checklists by card",
            description = "Retrieve all checklists belonging to a specific card",
            parameters = {
                    @Parameter(
                            name = "cardId",
                            description = "Card ID",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Checklists retrieved successfully",
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
    public ResponseEntity<?> get(@RequestParam String cardId) {
        List<ChecklistResponse> checklists = checklistQueryService.findByCardId(cardId);
        Response res = Response.builder()
                .code(SuccessMessageCode.CHECKLIST_FOUND)
                .message(mr.resolve(SuccessMessageCode.CHECKLIST_FOUND))
                .success(true)
                .data(checklists)
                .build();

        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Create checklist",
            description = "Create a new checklist for a card",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Checklist creation payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ChecklistCreateRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Checklist created successfully",
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
    public ResponseEntity<?> create(@RequestBody @Valid ChecklistCreateRequest request, @AuthenticationPrincipal Jwt jwt) {
        String createdBy = BuildCreatedByFromJwt.execute(userQueryService, mr, jwt);
        request.setCreatedBy(createdBy);

        ChecklistCreateResponse checklist = checklistCommandService.create(request);
        Response res = Response.builder()
                .success(true)
                .data(checklist)
                .message(mr.resolve(SuccessMessageCode.CHECKLIST_CREATED_SUCCESS))
                .code(SuccessMessageCode.CHECKLIST_CREATED_SUCCESS)
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Update checklist",
            description = "Update checklist information by ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Checklist update payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ChecklistUpdateRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Checklist updated successfully",
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
                            description = "Checklist not found",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody ChecklistUpdateRequest request, @PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String updatedBy = BuildCreatedByFromJwt.execute(userQueryService, mr, jwt);
        request.setUpdatedBy(updatedBy);
        request.setId(id);

        ChecklistUpdateResponse checklist = checklistCommandService.update(request);
        Response res = Response.builder()
                .code(SuccessMessageCode.CHECKLIST_UPDATED_SUCCESS)
                .message(mr.resolve(SuccessMessageCode.CHECKLIST_UPDATED_SUCCESS))
                .success(true)
                .data(checklist)
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Delete checklist",
            description = "Delete a checklist by ID",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Checklist ID",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Checklist deleted successfully",
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
                            description = "Checklist not found",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        checklistCommandService.delete(id);
        Response res = Response.builder()
                .code(SuccessMessageCode.CHECKLIST_DELETED_SUCCESS)
                .message(mr.resolve(SuccessMessageCode.CHECKLIST_DELETED_SUCCESS))
                .success(true)
                .data(null)
                .build();
        return ResponseEntity.ok(res);
    }
}

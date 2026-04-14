package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.request.ListCreateRequest;
import com.nhantic.trelloapi.dto.request.ListUpdateRequest;
import com.nhantic.trelloapi.dto.response.ListCreateResponse;
import com.nhantic.trelloapi.dto.response.ListUpdateResponse;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.event.ListCreateEvent;
import com.nhantic.trelloapi.helper.BuildCreatedByFromJwt;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IListCommandService;
import com.nhantic.trelloapi.service.IUserQueryService;
import com.nhantic.trelloapi.ws.ListWsService;
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
@RequestMapping("/api/v1/lists")
@RequiredArgsConstructor
@Tag(name = "List", description = "Endpoints for list entity")
public class ListController {
    private final IListCommandService listCommandService;
    private final IUserQueryService userQueryService;
    private final MessageResolver mr;
    private final ListWsService listWsService;

    @Operation(
            summary = "Create list",
            description = "Create a new list inside a board",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List creation payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ListCreateRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List created successfully",
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
    public ResponseEntity<?> create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ListCreateRequest request) {
        String createdBy = BuildCreatedByFromJwt.execute(userQueryService, mr, jwt);
        request.setCreatedBy(createdBy);
        ListCreateResponse list = listCommandService.create(request);
        ListCreateEvent listCreateEvent = ListCreateEvent.builder()
                .position(list.getPosition())
                .boardId(list.getBoardId())
                .name(list.getName())
                .id(list.getId())
                .build();

        listWsService.broadcastCreated(listCreateEvent.getBoardId(), listCreateEvent);

        Response res = Response.builder()
                .success(true)
                .code(SuccessMessageCode.LIST_CREATED_SUCCESS)
                .message(mr.resolve(SuccessMessageCode.LIST_CREATED_SUCCESS))
                .data(list)
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Update list",
            description = "Update list information by ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List update payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ListUpdateRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List updated successfully",
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
                            description = "List not found",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @Parameter(description = "List ID", required = true)
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@AuthenticationPrincipal Jwt jwt, @PathVariable String id, @Valid @RequestBody ListUpdateRequest request) {
        String updatedBy = BuildCreatedByFromJwt.execute(userQueryService, mr, jwt);
        request.setUpdatedBy(updatedBy);
        request.setId(id);
        ListUpdateResponse list = listCommandService.update(request);
        Response res = Response.builder()
                .success(true)
                .message(mr.resolve(SuccessMessageCode.LIST_UPDATED_SUCCESS))
                .code(SuccessMessageCode.LIST_UPDATED_SUCCESS)
                .data(list)
                .build();
        return ResponseEntity.ok(res);
    }
}

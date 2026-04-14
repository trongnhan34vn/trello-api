package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.request.BoardCreateRequest;
import com.nhantic.trelloapi.dto.response.BoardCreateResponse;
import com.nhantic.trelloapi.dto.response.BoardResponse;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.event.BoardCreateEvent;
import com.nhantic.trelloapi.helper.BuildCreatedByFromJwt;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IBoardCommandService;
import com.nhantic.trelloapi.service.IBoardQueryService;
import com.nhantic.trelloapi.service.IUserQueryService;
import com.nhantic.trelloapi.ws.BoardWsService;
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
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
@Tag(name = "Board", description = "Endpoints for board entity")
public class BoardController {
    private final MessageResolver mr;
    private final IBoardCommandService boardCommandService;
    private final IBoardQueryService boardQueryService;
    private final IUserQueryService userQueryService;
    private final BoardWsService boardWsService;

    @Operation(
            summary = "Create board",
            description = "Create a new board inside a workspace",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Board creation payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = BoardCreateRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Board created successfully",
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
    public ResponseEntity<?> create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody BoardCreateRequest request) {
        String createdBy = BuildCreatedByFromJwt.execute(userQueryService, mr, jwt);
        request.setCreatedBy(createdBy);
        BoardCreateResponse board = boardCommandService.create(request);

        BoardCreateEvent boardCreateEvent = BoardCreateEvent.builder()
                .id(board.getId())
                .name(board.getName())
                .backgroundUrl(board.getBackgroundUrl())
                .workspaceId(board.getWorkspaceId())
                .members(board.getMembers())
                .build();
        boardWsService.broadcastCreated(boardCreateEvent.getWorkspaceId(), boardCreateEvent);

        Response res = Response.builder()
                .success(true)
                .message(mr.resolve(SuccessMessageCode.BOARD_CREATED_SUCCESS))
                .code(SuccessMessageCode.BOARD_CREATED_SUCCESS)
                .data(board)
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Get board",
            description = "Retrieve board details by ID",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Board ID",
                            required = true,
                            example = "board-123"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Board retrieved successfully",
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
                            description = "Board not found",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        BoardResponse board = boardQueryService.findById(id);
        Response res = Response.builder()
                .success(true)
                .message(mr.resolve(SuccessMessageCode.BOARD_FOUND))
                .code(SuccessMessageCode.BOARD_FOUND)
                .data(board)
                .build();
        return ResponseEntity.ok(res);
    }
}

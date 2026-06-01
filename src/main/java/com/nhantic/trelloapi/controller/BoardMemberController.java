package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.request.BoardMemberCreateRequest;
import com.nhantic.trelloapi.dto.response.BoardMemberCreateResponse;
import com.nhantic.trelloapi.dto.response.BoardMemberResponse;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.helper.GetUserFromJwt;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IBoardMemberCommandService;
import com.nhantic.trelloapi.service.IBoardMemberQueryService;
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
@RequestMapping("/api/v1/board-members")
@RequiredArgsConstructor
@Tag(name = "Board Member", description = "Endpoints for managing board members")
public class BoardMemberController {
    private final IBoardMemberQueryService boardMemberQueryService;
    private final IBoardMemberCommandService boardMemberCommandService;
    private final IUserQueryService userQueryService;
    private final MessageResolver mr;

    @Operation(
            summary = "Search board members",
            description = "Search for members within a specific board",
            parameters = {
                    @Parameter(
                            name = "boardId",
                            description = "Board ID",
                            required = true
                    ),
                    @Parameter(
                            name = "search",
                            description = "Search keyword (email or name)"
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
    public ResponseEntity<?> get(@RequestParam("boardId") String boardId, @RequestParam("search") String search) {
        List<BoardMemberResponse> members = boardMemberQueryService.searchBoardMembersByBoardId(boardId, search);
        Response response = Response.builder()
                .success(true)
                .code(SuccessMessageCode.BOARD_MEMBERS_FOUND)
                .message(mr.resolve(SuccessMessageCode.BOARD_MEMBERS_FOUND))
                .data(members)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Add members to board",
            description = "Add one or more members to a board",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Board member creation payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = BoardMemberCreateRequest.class)
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
    public ResponseEntity<?> create(@RequestBody @Valid BoardMemberCreateRequest request, @AuthenticationPrincipal Jwt jwt) {
        String createdBy = GetUserFromJwt.execute(userQueryService, mr, jwt);
        request.setCreatedBy(createdBy);
        List<BoardMemberCreateResponse> boards = boardMemberCommandService.create(request);
        Response response = Response.builder()
                .success(true)
                .code(SuccessMessageCode.BOARD_MEMBER_CREATED_SUCCESS)
                .message(mr.resolve(SuccessMessageCode.BOARD_MEMBER_CREATED_SUCCESS))
                .data(boards)
                .build();
        return ResponseEntity.ok(response);
    }
}

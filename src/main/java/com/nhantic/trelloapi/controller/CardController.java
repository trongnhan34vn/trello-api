package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.request.CardCreateRequest;
import com.nhantic.trelloapi.dto.request.CardUpdateRequest;
import com.nhantic.trelloapi.dto.response.CardCreateResponse;
import com.nhantic.trelloapi.dto.response.CardResponse;
import com.nhantic.trelloapi.dto.response.CardUpdateResponse;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.event.CardCreateEvent;
import com.nhantic.trelloapi.helper.BuildCreatedByFromJwt;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.ICardCommandService;
import com.nhantic.trelloapi.service.ICardQueryService;
import com.nhantic.trelloapi.service.IUserQueryService;
import com.nhantic.trelloapi.ws.CardWsService;
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
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
@Tag(name = "Card", description = "Endpoints for card entity")
public class CardController {
    private final IUserQueryService userQueryService;
    private final ICardCommandService cardCommandService;
    private final MessageResolver mr;
    private final CardWsService cardWsService;
    private final ICardQueryService cardQueryService;

    @Operation(
            summary = "Create card",
            description = "Create a new card inside a list",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Card creation payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CardCreateRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Card created successfully",
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
    public ResponseEntity<?> create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CardCreateRequest request) {
        String createdBy = BuildCreatedByFromJwt.execute(userQueryService, mr, jwt);
        request.setCreatedBy(createdBy);
        CardCreateResponse card = cardCommandService.create(request);

        CardCreateEvent cardCreateEvent = CardCreateEvent.builder()
                .id(card.getId())
                .createdBy(createdBy)
                .title(card.getTitle())
                .position(card.getPosition())
                .listId(card.getListId())
                .boardId(card.getBoardId())
                .build();
        cardWsService.broadcastCreated(cardCreateEvent.getBoardId(), cardCreateEvent);

        Response res = Response.builder()
                .code(SuccessMessageCode.CARD_CREATED_SUCCESS)
                .data(card)
                .message(mr.resolve(SuccessMessageCode.CARD_CREATED_SUCCESS))
                .success(true)
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Update card",
            description = "Update card information by ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Card update payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CardUpdateRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Card updated successfully",
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
                            description = "Card not found",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@AuthenticationPrincipal Jwt jwt, @RequestBody CardUpdateRequest request, @PathVariable String id) {
        String updatedBy = BuildCreatedByFromJwt.execute(userQueryService, mr, jwt);
        request.setUpdatedBy(updatedBy);
        request.setId(id);
        CardUpdateResponse card = cardCommandService.update(request);
        Response res = Response.builder()
                .code(SuccessMessageCode.CARD_UPDATED_SUCCESS)
                .data(card)
                .message(mr.resolve(SuccessMessageCode.CARD_UPDATED_SUCCESS))
                .success(true)
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Get cards by board",
            description = "Retrieve all cards belonging to a specific board",
            parameters = {
                    @Parameter(
                            name = "boardId",
                            description = "Board ID to filter cards",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cards retrieved successfully",
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
    public ResponseEntity<?> get(@RequestParam String boardId) {
        List<CardResponse> cards = cardQueryService.findByBoardId(boardId);
        Response res = Response.builder()
                .success(true)
                .code(SuccessMessageCode.CARD_FOUND)
                .message(mr.resolve(SuccessMessageCode.CARD_FOUND))
                .data(cards)
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Get card detail",
            description = "Retrieve detailed information of a card by ID",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Card ID",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Card retrieved successfully",
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
                            description = "Card not found",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> detail (@PathVariable String id) {
        CardResponse card = cardQueryService.findById(id);
        Response res = Response.builder()
                .success(true)
                .code(SuccessMessageCode.CARD_FOUND)
                .message(mr.resolve(SuccessMessageCode.CARD_FOUND))
                .data(card)
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Delete card",
            description = "Delete a card by ID",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Card ID",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Card deleted successfully",
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
                            description = "Card not found",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        cardCommandService.delete(id);
        Response res = Response.builder()
                .success(true)
                .code(SuccessMessageCode.CARD_DELETED_SUCCESS)
                .message(mr.resolve(SuccessMessageCode.CARD_DELETED_SUCCESS))
                .data(null)
                .build();
        return ResponseEntity.ok(res);
    }
}

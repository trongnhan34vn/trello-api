package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.request.CardCreateRequest;
import com.nhantic.trelloapi.dto.request.CardUpdateRequest;
import com.nhantic.trelloapi.dto.response.CardCreateResponse;
import com.nhantic.trelloapi.dto.response.CardUpdateResponse;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.event.CardCreateEvent;
import com.nhantic.trelloapi.helper.BuildCreatedByFromJwt;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.ICardCommandService;
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

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
@Tag(name = "Card", description = "Endpoints for card entity")
public class CardController {
    private final IUserQueryService userQueryService;
    private final ICardCommandService cardCommandService;
    private final MessageResolver mr;
    private final CardWsService cardWsService;

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
                .isInbox(card.isInbox())
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
    @Parameter(description = "Card ID", required = true)
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CardUpdateRequest request, @PathVariable String id) {
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
}

package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.request.CardMemberCreateRequest;
import com.nhantic.trelloapi.dto.response.CardMemberCreateResponse;
import com.nhantic.trelloapi.dto.response.CardMemberResponse;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.helper.BuildCreatedByFromJwt;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.ICardMemberCommandService;
import com.nhantic.trelloapi.service.ICardMemberQueryService;
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
@RequestMapping("/api/v1/card-members")
@RequiredArgsConstructor
@Tag(name = "Card Member", description = "Endpoints for managing card members")
public class CardMemberController {
    private final ICardMemberCommandService cardMemberCommandService;
    private final ICardMemberQueryService cardMemberQueryService;
    private final IUserQueryService userQueryService;
    private final MessageResolver mr;

    @Operation(
            summary = "Add member to card",
            description = "Assign a member to a specific card",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Card member creation payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CardMemberCreateRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Member added successfully",
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
    public ResponseEntity<?> create(@RequestBody @Valid CardMemberCreateRequest request, @AuthenticationPrincipal Jwt jwt) {
        String createdBy = BuildCreatedByFromJwt.execute(userQueryService, mr, jwt);
        request.setCreatedBy(createdBy);
        CardMemberCreateResponse member = cardMemberCommandService.create(request);
        Response response = Response.builder()
                .success(true)
                .message(mr.resolve(SuccessMessageCode.CARD_MEMBER_CREATED_SUCCESS))
                .code(SuccessMessageCode.CARD_MEMBER_CREATED_SUCCESS)
                .data(member)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get members of card",
            description = "Retrieve all members assigned to a specific card",
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
    public ResponseEntity<?> get(@RequestParam String cardId) {
        List<CardMemberResponse> members = cardMemberQueryService.findByCardId(cardId);
        Response res = Response.builder()
                .data(members)
                .success(true)
                .message(mr.resolve(SuccessMessageCode.CARD_MEMBER_FOUND))
                .code(SuccessMessageCode.CARD_MEMBER_FOUND)
                .build();
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        cardMemberCommandService.delete(id);
        Response res = Response.builder()
                .data(null)
                .success(true)
                .message(mr.resolve(SuccessMessageCode.CARD_MEMBER_DELETED_SUCCESS))
                .code(SuccessMessageCode.CARD_MEMBER_DELETED_SUCCESS)
                .build();
        return ResponseEntity.ok(res);
    }
}

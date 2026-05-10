package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IRoleService;
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

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Role", description = "Endpoints for retrieving roles")
public class RoleController {
    private final IRoleService roleService;
    private final MessageResolver mr;

    @Operation(
            summary = "Get all roles",
            description = "Retrieve a list of all available roles (e.g., ADMIN, MEMBER)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Roles retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Response.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<?> get() {
        Response res = Response.builder()
                .code(SuccessMessageCode.ROLE_FOUND)
                .message(mr.resolve(SuccessMessageCode.ROLE_FOUND))
                .data(roleService.findAll())
                .build();
        return ResponseEntity.ok(res);
    }
}

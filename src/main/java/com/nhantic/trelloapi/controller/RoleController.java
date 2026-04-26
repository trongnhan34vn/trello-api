package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final IRoleService roleService;
    private final MessageResolver mr;
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

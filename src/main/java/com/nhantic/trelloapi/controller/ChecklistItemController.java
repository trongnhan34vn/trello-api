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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/checklist-items")
@RequiredArgsConstructor
public class ChecklistItemController {
    private final IUserQueryService userQueryService;
    private final MessageResolver mr;
    private final IChecklistItemCommandService checklistItemCommandService;

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

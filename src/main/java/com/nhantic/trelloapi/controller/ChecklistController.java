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
public class ChecklistController {
    private final IChecklistQueryService checklistQueryService;
    private final IChecklistCommandService checklistCommandService;
    private final IUserQueryService userQueryService;
    private final MessageResolver mr;

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

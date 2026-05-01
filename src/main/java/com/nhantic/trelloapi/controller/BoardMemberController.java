package com.nhantic.trelloapi.controller;

import com.nhantic.trelloapi.constant.SuccessMessageCode;
import com.nhantic.trelloapi.dto.request.BoardMemberCreateRequest;
import com.nhantic.trelloapi.dto.response.BoardMemberCreateResponse;
import com.nhantic.trelloapi.dto.response.BoardMemberResponse;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.dto.response.WorkspaceMemberResponse;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.service.IBoardMemberCommandService;
import com.nhantic.trelloapi.service.IBoardMemberQueryService;
import com.nhantic.trelloapi.service.IWorkspaceMemberQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/board-members")
@RequiredArgsConstructor
public class BoardMemberController {
    private final IBoardMemberQueryService boardMemberQueryService;
    private final IBoardMemberCommandService boardMemberCommandService;
    private final MessageResolver mr;

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

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody @Valid BoardMemberCreateRequest request) {
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

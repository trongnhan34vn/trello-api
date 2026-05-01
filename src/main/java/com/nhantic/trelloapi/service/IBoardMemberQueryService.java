package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.BoardMemberResponse;

import java.util.List;

public interface IBoardMemberQueryService {
    List<BoardMemberResponse> searchBoardMembersByBoardId(String boardId, String search);
}

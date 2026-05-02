package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.request.BoardMemberCreateRequest;
import com.nhantic.trelloapi.dto.response.BoardMemberCreateResponse;

import java.util.List;

public interface IBoardMemberCommandService {
    List<BoardMemberCreateResponse> create(BoardMemberCreateRequest request);
}

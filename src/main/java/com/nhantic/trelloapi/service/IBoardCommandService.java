package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.request.BoardCreateRequest;
import com.nhantic.trelloapi.dto.response.BoardCreateResponse;

public interface IBoardCommandService {
    BoardCreateResponse create(BoardCreateRequest request);
}

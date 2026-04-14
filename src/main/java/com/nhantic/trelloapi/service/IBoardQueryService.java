package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.BoardResponse;

public interface IBoardQueryService {
    BoardResponse searchById(String id);
    BoardResponse findById(String id);
}

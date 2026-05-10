package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.BoardResponse;

import java.util.List;

public interface IBoardQueryService {
    BoardResponse searchById(String id);
    BoardResponse findById(String id);
    List<BoardResponse> searchByCognitoId(String cognitoId, String search);
}

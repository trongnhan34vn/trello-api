package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.CardResponse;

import java.util.List;

public interface ICardQueryService {
    CardResponse findById(String id);
    List<CardResponse> findByBoardId(String boardId);
}

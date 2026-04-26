package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.ListResponse;

import java.util.List;

public interface IListQueryService {
    ListResponse findById(String id);
    List<ListResponse> findByBoardId(String boardId);
}

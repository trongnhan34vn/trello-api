package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.ListResponse;

public interface IListQueryService {
    ListResponse findById(String id);
}

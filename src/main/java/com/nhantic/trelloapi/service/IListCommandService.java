package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.request.ListCreateRequest;
import com.nhantic.trelloapi.dto.request.ListUpdateRequest;
import com.nhantic.trelloapi.dto.response.ListCreateResponse;
import com.nhantic.trelloapi.dto.response.ListUpdateResponse;

public interface IListCommandService {
    ListCreateResponse create(ListCreateRequest request);
    ListUpdateResponse update(ListUpdateRequest request);
    void delete(String id);
}

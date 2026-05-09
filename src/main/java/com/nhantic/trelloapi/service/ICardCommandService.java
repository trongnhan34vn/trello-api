package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.request.CardCreateRequest;
import com.nhantic.trelloapi.dto.request.CardUpdateRequest;
import com.nhantic.trelloapi.dto.response.CardCreateResponse;
import com.nhantic.trelloapi.dto.response.CardUpdateResponse;

public interface ICardCommandService {
    CardCreateResponse create(CardCreateRequest request);
    CardUpdateResponse update(CardUpdateRequest request);
    void delete(String id);
}

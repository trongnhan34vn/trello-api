package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.CardResponse;

public interface ICardQueryService {
    CardResponse findById(String id);
}

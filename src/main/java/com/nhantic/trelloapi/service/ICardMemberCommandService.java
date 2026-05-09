package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.request.CardMemberCreateRequest;
import com.nhantic.trelloapi.dto.response.CardMemberCreateResponse;

public interface ICardMemberCommandService {
    CardMemberCreateResponse create(CardMemberCreateRequest request);
    void delete(String id);

}

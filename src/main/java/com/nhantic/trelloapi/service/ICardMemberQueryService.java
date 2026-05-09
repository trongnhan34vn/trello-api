package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.CardMemberResponse;

import java.util.List;

public interface ICardMemberQueryService {
    List<CardMemberResponse> findByCardId(String id);
}

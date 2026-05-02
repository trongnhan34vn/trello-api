package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.ChecklistResponse;

import java.util.List;

public interface IChecklistQueryService {
    List<ChecklistResponse> findByCardId(String cardId);
}

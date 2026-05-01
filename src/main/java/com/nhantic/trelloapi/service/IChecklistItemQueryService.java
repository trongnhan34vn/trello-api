package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.ChecklistItemResponse;

import java.util.List;

public interface IChecklistItemQueryService {
    List<ChecklistItemResponse> findByChecklistId (String checklistId);
}

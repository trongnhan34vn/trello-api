package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.request.ChecklistCreateRequest;
import com.nhantic.trelloapi.dto.request.ChecklistUpdateRequest;
import com.nhantic.trelloapi.dto.response.ChecklistCreateResponse;
import com.nhantic.trelloapi.dto.response.ChecklistUpdateResponse;

public interface IChecklistCommandService {
    ChecklistCreateResponse create (ChecklistCreateRequest request);
    ChecklistUpdateResponse update (ChecklistUpdateRequest request);
    void delete(String id);
}

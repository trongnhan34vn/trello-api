package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.request.ChecklistItemCreateRequest;
import com.nhantic.trelloapi.dto.request.ChecklistItemUpdateRequest;
import com.nhantic.trelloapi.dto.response.ChecklistItemCreateResponse;
import com.nhantic.trelloapi.dto.response.ChecklistItemUpdateResponse;

public interface IChecklistItemCommandService {
    ChecklistItemCreateResponse create(ChecklistItemCreateRequest request);
    ChecklistItemUpdateResponse update(ChecklistItemUpdateRequest request);
}

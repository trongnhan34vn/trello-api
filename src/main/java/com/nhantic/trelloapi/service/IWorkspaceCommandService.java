package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.request.WorkspaceCreateRequest;
import com.nhantic.trelloapi.dto.response.WorkspaceCreateResponse;

public interface IWorkspaceCommandService {
    WorkspaceCreateResponse create(WorkspaceCreateRequest req);
}

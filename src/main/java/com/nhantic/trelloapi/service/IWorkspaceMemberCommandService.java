package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.request.WorkspaceMemberCreateRequest;
import com.nhantic.trelloapi.dto.response.WorkspaceMemberCreateResponse;

import java.util.List;

public interface IWorkspaceMemberCommandService {
    List<WorkspaceMemberCreateResponse> create (WorkspaceMemberCreateRequest request);
}

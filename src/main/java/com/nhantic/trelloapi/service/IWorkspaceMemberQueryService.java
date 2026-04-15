package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.WorkspaceMemberResponse;

import java.util.List;

public interface IWorkspaceMemberQueryService {
    List<WorkspaceMemberResponse> listMembersByWorkspaceId(String workspaceId);
}

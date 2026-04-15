package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.WorkspaceResponse;

import java.util.List;

public interface IWorkspaceQueryService {
    List<WorkspaceResponse> searchByCognitoId(String userId, String search);
    WorkspaceResponse findById(String id);
    WorkspaceResponse searchByCognitoIdAndWorkspaceId(String cognitoId ,String workspaceId);
}

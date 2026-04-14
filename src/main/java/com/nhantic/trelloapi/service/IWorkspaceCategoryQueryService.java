package com.nhantic.trelloapi.service;

import com.nhantic.trelloapi.dto.response.WorkspaceCategoryResponse;

import java.util.List;

public interface IWorkspaceCategoryQueryService {
    List<WorkspaceCategoryResponse> findAll();
    WorkspaceCategoryResponse findById(Integer id);
}

package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.dto.response.WorkspaceCategoryResponse;
import com.nhantic.trelloapi.entity.WorkspaceCategory;
import com.nhantic.trelloapi.exception.NotFoundException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.repository.IWorkspaceCategoryRepository;
import com.nhantic.trelloapi.service.IWorkspaceCategoryQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceCategoryServiceImpl implements IWorkspaceCategoryQueryService {
    private final IWorkspaceCategoryRepository workspaceCategoryRepository;
    private final MessageResolver mr;

    @Override
    public List<WorkspaceCategoryResponse> findAll() {
        try {
            log.info("[WorkspaceCategory][findAll]: Start");
            List<WorkspaceCategory> wcs = workspaceCategoryRepository.findAll();
            if (wcs.isEmpty()) {
                throw new NotFoundException(ErrorMessageCode.WORKSPACE_CATEGORY_NOT_FOUND, mr.resolve(ErrorMessageCode.WORKSPACE_CATEGORY_NOT_FOUND));
            }
            log.info("[WorkspaceCategory][findAll]: Found {} workspace categories", wcs.size());
            return wcs.stream().map(w -> (
                    WorkspaceCategoryResponse.builder()
                            .id(w.getId())
                            .name(w.getName())
                            .build()
            )).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("[WorkspaceCategory][findAll]: Error", e);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public WorkspaceCategoryResponse findById(Integer id) {
        try {
            log.info("[WorkspaceCategory][findById]: Start");
            WorkspaceCategory wc = workspaceCategoryRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessageCode.WORKSPACE_CATEGORY_NOT_FOUND, mr.resolve(ErrorMessageCode.WORKSPACE_CATEGORY_NOT_FOUND)));
            log.info("[WorkspaceCategory][findById]: Found {} workspace categories", wc.getId());
            return WorkspaceCategoryResponse.builder()
                    .id(wc.getId())
                    .name(wc.getName())
                    .build();
        } catch (Exception e) {
            log.error("[WorkspaceCategory][findById]: Error", e);
            e.printStackTrace();
            throw e;
        }
    }
}

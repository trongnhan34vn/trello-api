package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.dto.request.ChecklistItemCreateRequest;
import com.nhantic.trelloapi.dto.request.ChecklistItemUpdateRequest;
import com.nhantic.trelloapi.dto.response.ChecklistItemCreateResponse;
import com.nhantic.trelloapi.dto.response.ChecklistItemResponse;
import com.nhantic.trelloapi.dto.response.ChecklistItemUpdateResponse;
import com.nhantic.trelloapi.entity.Checklist;
import com.nhantic.trelloapi.entity.ChecklistItem;
import com.nhantic.trelloapi.exception.BadRequestException;
import com.nhantic.trelloapi.exception.NotFoundException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.repository.IChecklistItemRepository;
import com.nhantic.trelloapi.repository.IChecklistRepository;
import com.nhantic.trelloapi.service.IChecklistItemCommandService;
import com.nhantic.trelloapi.service.IChecklistItemQueryService;
import com.nhantic.trelloapi.util.DatetimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistItemServiceImpl implements IChecklistItemQueryService, IChecklistItemCommandService {
    private final IChecklistRepository checklistRepository;
    private final IChecklistItemRepository checklistItemRepository;
    private final MessageResolver mr;

    @Override
    @Transactional
    public ChecklistItemCreateResponse create(ChecklistItemCreateRequest request) {
        try {
            log.info("[ChecklistItem][create] Start: request=[{}]", request);
            Checklist checklist = checklistRepository.findById(UUID.fromString(request.getChecklistId())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.CHECKLIST_NOT_FOUND, mr.resolve(ErrorMessageCode.CHECKLIST_NOT_FOUND)));
            ChecklistItem preCreatedChecklistItem = ChecklistItem.builder()
                    .name(request.getName())
                    .position(request.getPosition())
                    .checklist(checklist)
                    .createdBy(UUID.fromString(request.getCreatedBy()))
                    .build();
            if (request.getDueDate() != null) {
                preCreatedChecklistItem.setDueDate(DatetimeUtil.parse(request.getDueDate()));
            }

            ChecklistItem createdChecklistItem = checklistItemRepository.save(preCreatedChecklistItem);
            log.info("[ChecklistItem][create] Success: response=[{}]", createdChecklistItem);
            return ChecklistItemCreateResponse.builder()
                    .id(createdChecklistItem.getId().toString())
                    .name(createdChecklistItem.getName())
                    .position(createdChecklistItem.getPosition())
                    .checklistId(createdChecklistItem.getChecklist().getId().toString())
                    .isCompleted(createdChecklistItem.isCompleted())
                    .build();
        } catch (Exception e) {
            log.error("[ChecklistItem][create] Error: error=[{}]", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public ChecklistItemUpdateResponse update(ChecklistItemUpdateRequest request) {
        String id = request.getId();
        String name = request.getName();
        String position = request.getPosition();
        String dueDate = request.getDueDate();
        String checklistId = request.getChecklistId();
        String updatedBy = request.getUpdatedBy();
        boolean isCompleted = request.isCompleted();

        boolean isNeedUpdate = false;
        try {
            log.info("[ChecklistItem][update] Start: request=[{}]", request);
            ChecklistItem preUpdatedChecklistItem = checklistItemRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException(ErrorMessageCode.CHECKLIST_ITEM_NOT_FOUND, mr.resolve(ErrorMessageCode.CHECKLIST_ITEM_NOT_FOUND)));

            if (name != null && !name.isEmpty()) {
                preUpdatedChecklistItem.setName(name);
                isNeedUpdate = true;
            }

            if (position != null) {
                preUpdatedChecklistItem.setPosition(position);
                isNeedUpdate = true;
            }

            if (dueDate != null) {
                preUpdatedChecklistItem.setDueDate(DatetimeUtil.parse(dueDate));
                isNeedUpdate = true;
            }

            if (checklistId != null) {
                Checklist checklist = checklistRepository.findById(UUID.fromString(checklistId)).orElseThrow(() -> new NotFoundException(ErrorMessageCode.CHECKLIST_NOT_FOUND, mr.resolve(ErrorMessageCode.CHECKLIST_NOT_FOUND)));
                preUpdatedChecklistItem.setChecklist(checklist);
                isNeedUpdate = true;
            }

            if (isCompleted != preUpdatedChecklistItem.isCompleted()) {
                preUpdatedChecklistItem.setCompleted(isCompleted);
                isNeedUpdate = true;
            }

            if (isNeedUpdate) {
                preUpdatedChecklistItem.setUpdatedBy(UUID.fromString(updatedBy));
            } else {
                return null;
            }

            ChecklistItem updatedChecklistItem = checklistItemRepository.save(preUpdatedChecklistItem);
            log.info("[ChecklistItem][update] Success: res=[{}]", updatedChecklistItem);

            return ChecklistItemUpdateResponse.builder()
                    .id(updatedChecklistItem.getId().toString())
                    .checklistId(updatedChecklistItem.getChecklist().getId().toString())
                    .name(updatedChecklistItem.getName())
                    .position(updatedChecklistItem.getPosition())
                    .dueDate(updatedChecklistItem.getDueDate() != null ? updatedChecklistItem.getDueDate().toString() : null)
                    .updateBy(updatedChecklistItem.getUpdatedBy() != null ? updatedChecklistItem.getUpdatedBy().toString() : null)
                    .updatedAt(updatedChecklistItem.getUpdatedAt() != null ? updatedChecklistItem.getUpdatedAt().toString() : null)
                    .createdAt(updatedChecklistItem.getCreatedAt() != null ? updatedChecklistItem.getCreatedAt().toString() : null)
                    .createdBy(updatedChecklistItem.getCreatedBy() != null ? updatedChecklistItem.getCreatedBy().toString() : null)
                    .build();

        } catch (Exception e) {
            log.error("[ChecklistItem][update] Error: error=[{}]", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void delete(String id) {
        try {
            log.info("[ChecklistItem][delete] Start: request=[{}]", id);
            checklistItemRepository.deleteById(UUID.fromString(id));
            log.info("[ChecklistItem][delete] Success");
        } catch (Exception e) {
            log.error("[ChecklistItem][delete] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<ChecklistItemResponse> findByChecklistId(String checklistId) {
        try {
            log.info("[ChecklistItem][findByChecklistId] Start: request=[{}]", checklistId);
            List<ChecklistItem> checklistItems = checklistItemRepository.findByChecklist_Id(UUID.fromString(checklistId));
            log.info("[ChecklistItem][create] Success: found=[{}]", checklistItems.size());
            return checklistItems.stream().map(ci -> (ChecklistItemResponse.builder()
                    .id(ci.getId().toString())
                    .name(ci.getName())
                    .position(ci.getPosition())
                    .checklistId(ci.getChecklist().getId().toString())
                    .createdAt(ci.getCreatedAt() != null ? ci.getCreatedAt().toString() : null)
                    .updatedAt(ci.getUpdatedAt() != null ? ci.getUpdatedAt().toString() : null)
                    .createdBy(ci.getCreatedBy() != null ? ci.getCreatedBy().toString() : null)
                    .updatedBy(ci.getUpdatedBy() != null ? ci.getUpdatedBy().toString() : null)
                    .build())).toList();
        } catch (Exception e) {
            log.error("[ChecklistItem][findByChecklistId] Error: error=[{}]", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


}

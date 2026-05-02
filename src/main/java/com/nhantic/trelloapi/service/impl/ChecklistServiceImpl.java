package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.dto.request.ChecklistCreateRequest;
import com.nhantic.trelloapi.dto.request.ChecklistUpdateRequest;
import com.nhantic.trelloapi.dto.response.ChecklistCreateResponse;
import com.nhantic.trelloapi.dto.response.ChecklistItemResponse;
import com.nhantic.trelloapi.dto.response.ChecklistResponse;
import com.nhantic.trelloapi.dto.response.ChecklistUpdateResponse;
import com.nhantic.trelloapi.entity.Card;
import com.nhantic.trelloapi.entity.Checklist;
import com.nhantic.trelloapi.exception.NotFoundException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.repository.ICardRepository;
import com.nhantic.trelloapi.repository.IChecklistRepository;
import com.nhantic.trelloapi.service.IChecklistCommandService;
import com.nhantic.trelloapi.service.IChecklistQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements IChecklistQueryService, IChecklistCommandService {
    private final IChecklistRepository checklistRepository;
    private final ICardRepository cardRepository;
    private final MessageResolver mr;

    @Override
    @Transactional
    public ChecklistCreateResponse create(ChecklistCreateRequest request) {
        try {
            log.info("[Checklist][create] Start: request=[{}]", request);
            Card card = cardRepository.findById(UUID.fromString(request.getCardId())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.CARD_NOT_FOUND, mr.resolve(ErrorMessageCode.CARD_NOT_FOUND)));

            Checklist preCreatedChecklist = Checklist.builder()
                    .name(request.getName())
                    .position(request.getPosition())
                    .card(card)
                    .createdBy(UUID.fromString(request.getCreatedBy()))
                    .build();

            Checklist createdChecklist = checklistRepository.save(preCreatedChecklist);
            log.info("[Checklist][create] Success: created=[{}]", createdChecklist);

            return ChecklistCreateResponse.builder()
                    .id(createdChecklist.getId().toString())
                    .name(createdChecklist.getName())
                    .position(createdChecklist.getPosition())
                    .cardId(createdChecklist.getCard().getId().toString())
                    .build();
        } catch (Exception e) {
            log.error("[Checklist][create] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public ChecklistUpdateResponse update(ChecklistUpdateRequest request) {
        String id = request.getId();
        String name = request.getName();
        String position = request.getPosition();
        String updatedBy = request.getUpdatedBy();

        boolean isNeedUpdated = false;
        try {
            log.info("[Checklist][update] Start: request=[{}]", request);
            Checklist preUpdatedChecklist = checklistRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException(ErrorMessageCode.CHECKLIST_NOT_FOUND, mr.resolve(ErrorMessageCode.CHECKLIST_NOT_FOUND)));

            if (name != null && !name.isEmpty()) {
                preUpdatedChecklist.setName(name);
                isNeedUpdated = true;
            }

            if (position != null) {
                preUpdatedChecklist.setPosition(position);
                isNeedUpdated = true;
            }

            if (isNeedUpdated) {
                preUpdatedChecklist.setUpdatedBy(UUID.fromString(updatedBy));
            } else {
                return null;
            }

            Checklist updatedChecklist = checklistRepository.save(preUpdatedChecklist);
            return ChecklistUpdateResponse.builder()
                    .id(updatedChecklist.getId().toString())
                    .name(updatedChecklist.getName())
                    .cardId(updatedChecklist.getCard().getId().toString())
                    .position(updatedChecklist.getPosition())
                    .updatedBy(updatedChecklist.getUpdatedBy() != null ? updatedChecklist.getUpdatedBy().toString() : null)
                    .updatedAt(updatedChecklist.getUpdatedAt() != null ? updatedChecklist.getUpdatedAt().toString() : null)
                    .createdBy(updatedChecklist.getCreatedBy() != null ? updatedChecklist.getCreatedBy().toString() : null)
                    .createdAt(updatedChecklist.getCreatedAt() != null ? updatedChecklist.getCreatedAt().toString() : null)
                    .build();

        } catch (Exception e) {
            log.error("[Checklist][update] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void delete(String id) {
        try {
            log.info("[Checklist][delete] Start: request=[{}]", id);
            checklistRepository.deleteById(UUID.fromString(id));
            log.info("[Checklist][delete] Success");
        } catch (Exception e) {
            log.error("[Checklist][delete] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<ChecklistResponse> findByCardId(String cardId) {
        try {
            log.info("[Checklist][findByCardId] Start: request=[{}]", cardId);
            List<Checklist> checklists = checklistRepository.findByCard_Id(UUID.fromString(cardId));
            log.info("[Checklist][findByCardId] Success: found=[{} items]", checklists.size());

            return checklists.stream().map(c -> (ChecklistResponse.builder()
                    .id(c.getId().toString())
                    .name(c.getName())
                    .cardId(c.getCard().getId().toString())
                    .position(c.getPosition())
                    .createdAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null)
                    .updatedAt(c.getUpdatedAt() != null ? c.getUpdatedAt().toString() : null)
                    .createdBy(c.getCreatedBy() != null ? c.getCreatedBy().toString() : null)
                    .updatedBy(c.getUpdatedBy() != null ? c.getUpdatedBy().toString() : null)
                    .checklistItems(c.getChecklistItems().stream().map(ci -> (ChecklistItemResponse.builder()
                            .id(ci.getId().toString())
                            .name(ci.getName())
                            .isCompleted(ci.isCompleted())
                            .checklistId(ci.getChecklist().getId().toString())
                            .position(ci.getPosition())
                            .dueDate(ci.getDueDate() != null ? ci.getDueDate().toString() : null)
                            .build())).toList())
                    .build())).toList();
        } catch (Exception e) {
            log.error("[Checklist][findByCardId] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}

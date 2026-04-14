package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.dto.request.CardCreateRequest;
import com.nhantic.trelloapi.dto.request.CardUpdateRequest;
import com.nhantic.trelloapi.dto.response.CardCreateResponse;
import com.nhantic.trelloapi.dto.response.CardResponse;
import com.nhantic.trelloapi.dto.response.CardUpdateResponse;
import com.nhantic.trelloapi.entity.Card;
import com.nhantic.trelloapi.entity.List;
import com.nhantic.trelloapi.exception.NotFoundException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.repository.ICardRepository;
import com.nhantic.trelloapi.repository.IListRepository;
import com.nhantic.trelloapi.service.ICardCommandService;
import com.nhantic.trelloapi.service.ICardQueryService;
import com.nhantic.trelloapi.util.DatetimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements ICardCommandService, ICardQueryService {
    private final ICardRepository cardRepository;
    private final IListRepository listRepository;
    private final MessageResolver mr;

    @Override
    public CardCreateResponse create(CardCreateRequest request) {
        try {
            log.info("[Card][create] Start");
            Card preCreateCard = new Card();
            List list = listRepository.findById(UUID.fromString(request.getListId())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.LIST_NOT_FOUND, mr.resolve(ErrorMessageCode.LIST_NOT_FOUND)));
            preCreateCard.setTitle(request.getTitle());
            preCreateCard.setCreatedBy(UUID.fromString(request.getCreatedBy()));
            preCreateCard.setPosition(request.getPosition());
            if (request.isInbox()) {
                preCreateCard.setInbox(true);
            } else {
                preCreateCard.setList(list);
                preCreateCard.setInbox(false);
            }
            Card createdCard = cardRepository.save(preCreateCard);
            log.info("[Card][create] Success");
            return CardCreateResponse.builder()
                    .title(createdCard.getTitle())
                    .position(createdCard.getPosition())
                    .createdBy(createdCard.getCreatedBy().toString())
                    .isInbox(createdCard.isInbox())
                    .id(createdCard.getId().toString())
                    .listId(createdCard.getList().getId().toString())
                    .boardId(list.getBoard().getId().toString())
                    .build();
        } catch (Exception e) {
            log.error("[Card][create] Error", e);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public CardUpdateResponse update(CardUpdateRequest request) {
        try {
            log.info("[Card][update] Start");
            Card preUpdateCard = Card.builder()
                    .id(UUID.fromString(request.getId()))
                    .title(request.getTitle())
                    .position(request.getPosition())
                    .isInbox(request.isInbox())
                    .description(request.getDescription())
                    .startDate(DatetimeUtil.parse(request.getStartDate()))
                    .dueDate(DatetimeUtil.parse(request.getDueDate()))
                    .updatedBy(UUID.fromString(request.getUpdatedBy()))
                    .updatedAt(DatetimeUtil.now())
                    .build();

            if (request.getListId() != null) {
                List list = listRepository.findById(UUID.fromString(request.getListId())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.LIST_NOT_FOUND, mr.resolve(ErrorMessageCode.LIST_NOT_FOUND)));
                preUpdateCard.setList(list);
            }
            Card updatedCard = cardRepository.save(preUpdateCard);
            log.info("[Card][update] Success");
            return CardUpdateResponse.builder()
                    .id(updatedCard.getId().toString())
                    .title(updatedCard.getTitle())
                    .position(updatedCard.getPosition())
                    .isInbox(updatedCard.isInbox())
                    .description(updatedCard.getDescription())
                    .dueDate(DatetimeUtil.parse(updatedCard.getDueDate()))
                    .startDate(DatetimeUtil.parse(updatedCard.getStartDate()))
                    .updatedBy(updatedCard.getUpdatedBy().toString())
                    .build();
        } catch (Exception e) {
            log.error("[Card][update] Error", e);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public CardResponse findById(String id) {
        try {
            log.info("[Card][findById] Start");
            Card card = cardRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException(ErrorMessageCode.CARD_NOT_FOUND, mr.resolve(ErrorMessageCode.CARD_NOT_FOUND)));
            log.info("[Card][findById] Success");
            return CardResponse.builder()
                    .id(card.getId().toString())
                    .title(card.getTitle())
                    .build();
        } catch (Exception e) {
            log.error("[Card][findById] Error", e);
            e.printStackTrace();
            throw e;
        }
    }
}

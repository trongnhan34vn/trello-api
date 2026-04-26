package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.constant.RoleName;
import com.nhantic.trelloapi.dto.request.CardCreateRequest;
import com.nhantic.trelloapi.dto.request.CardUpdateRequest;
import com.nhantic.trelloapi.dto.response.CardCreateResponse;
import com.nhantic.trelloapi.dto.response.CardResponse;
import com.nhantic.trelloapi.dto.response.CardUpdateResponse;
import com.nhantic.trelloapi.entity.*;
import com.nhantic.trelloapi.exception.NotFoundException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.repository.*;
import com.nhantic.trelloapi.service.ICardCommandService;
import com.nhantic.trelloapi.service.ICardQueryService;
import com.nhantic.trelloapi.util.DatetimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
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
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final ICardMemberRepository cardMemberRepository;

    @Override
    @Transactional
    public CardCreateResponse create(CardCreateRequest request) {
        try {
            log.info("[Card][create] Start {}", request);
            Card preCreateCard = new Card();
            List list = listRepository.findById(UUID.fromString(request.getListId())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.LIST_NOT_FOUND, mr.resolve(ErrorMessageCode.LIST_NOT_FOUND)));
            preCreateCard.setTitle(request.getTitle());
            preCreateCard.setList(list);
            preCreateCard.setCreatedBy(UUID.fromString(request.getCreatedBy()));
            preCreateCard.setPosition(request.getPosition());
            Card createdCard = cardRepository.save(preCreateCard);

            User user = userRepository.findById(UUID.fromString(request.getCreatedBy())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.USER_NOT_FOUND, mr.resolve(ErrorMessageCode.USER_NOT_FOUND)));
            Role role = roleRepository.findByName(RoleName.ADMIN).orElseThrow(() -> new NotFoundException(ErrorMessageCode.ROLE_NOT_FOUND, mr.resolve(ErrorMessageCode.ROLE_NOT_FOUND)));
            CardMember member = CardMember.builder()
                    .user(user)
                    .role(role)
                    .card(createdCard)
                    .build();
            cardMemberRepository.save(member);

            log.info("[Card][create] Success");
            return CardCreateResponse.builder()
                    .title(createdCard.getTitle())
                    .position(createdCard.getPosition())
                    .createdBy(createdCard.getCreatedBy().toString())
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
            Card preUpdateCard = cardRepository.findById(UUID.fromString(request.getId())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.CARD_NOT_FOUND, mr.resolve(ErrorMessageCode.CARD_NOT_FOUND)));

            if (request.getTitle() != null && !request.getTitle().isEmpty()) {
                preUpdateCard.setTitle(request.getTitle());
            }

            if (request.getPosition() != 0) {
                preUpdateCard.setPosition(request.getPosition());
            }

            if (request.getDescription() != null && !request.getDescription().isEmpty()) {
                preUpdateCard.setDescription(request.getDescription());
            }

            if (request.getDueDate() != null) {
                preUpdateCard.setDueDate(DatetimeUtil.parse(request.getDueDate()));
            }

            if (request.getStartDate() != null) {
                preUpdateCard.setStartDate(DatetimeUtil.parse(request.getStartDate()));
            }

            preUpdateCard.setUpdatedBy(UUID.fromString(request.getUpdatedBy()));

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
                    .description(updatedCard.getDescription())
                    .dueDate(updatedCard.getDueDate() != null ? DatetimeUtil.parse(updatedCard.getDueDate()) : null)
                    .startDate(updatedCard.getStartDate() != null ? DatetimeUtil.parse(updatedCard.getStartDate()) : null)
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

    @Override
    public java.util.List<CardResponse> findByBoardId(String boardId) {
        java.util.List<List> lists = listRepository.findListByBoard_Id(UUID.fromString(boardId), Sort.by("position"));
        java.util.List<UUID> listIds = lists.stream().map(List::getId).toList();
        java.util.List<Card> cards = cardRepository.findCardByListIdIn(listIds);
        return cards.stream().map(c -> (
                CardResponse.builder()
                        .id(c.getId().toString())
                        .title(c.getTitle())
                        .listId(c.getList().getId().toString())
                        .startDate(c.getStartDate() != null ? c.getStartDate().toString() : null)
                        .dueDate(c.getDueDate() != null ? c.getDueDate().toString() : null)
                        .description(c.getDescription())
                        .position(c.getPosition())
                        .updatedBy(c.getUpdatedBy() != null ? c.getUpdatedBy().toString() : null)
                        .build()
        )).toList();
    }
}

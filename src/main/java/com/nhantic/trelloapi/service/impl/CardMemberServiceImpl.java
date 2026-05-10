package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.dto.request.CardMemberCreateRequest;
import com.nhantic.trelloapi.dto.response.CardMemberCreateResponse;
import com.nhantic.trelloapi.dto.response.CardMemberResponse;
import com.nhantic.trelloapi.entity.Card;
import com.nhantic.trelloapi.entity.CardMember;
import com.nhantic.trelloapi.entity.User;
import com.nhantic.trelloapi.exception.NotFoundException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.repository.ICardMemberRepository;
import com.nhantic.trelloapi.repository.ICardRepository;
import com.nhantic.trelloapi.repository.IUserRepository;
import com.nhantic.trelloapi.service.ICardMemberCommandService;
import com.nhantic.trelloapi.service.ICardMemberQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardMemberServiceImpl implements ICardMemberCommandService, ICardMemberQueryService {
    private final ICardRepository cardRepository;
    private final MessageResolver mr;
    private final IUserRepository userRepository;
    private final ICardMemberRepository cardMemberRepository;

    @Override
    @Transactional
    public CardMemberCreateResponse create(CardMemberCreateRequest request) {
        try {
            log.info("[CardMember][create]: Start: req=[{}]", request);
            Card card = cardRepository.findById(UUID.fromString(request.getCardId())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.CARD_NOT_FOUND, mr.resolve(ErrorMessageCode.CARD_NOT_FOUND)));
            User user = userRepository.findById(UUID.fromString(request.getUserId())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.USER_NOT_FOUND, mr.resolve(ErrorMessageCode.USER_NOT_FOUND)));
            CardMember preCreatedCardMember = CardMember.builder()
                    .card(card)
                    .user(user)
                    .createdBy(UUID.fromString(request.getCreatedBy()))
                    .build();
            CardMember createdCardMember = cardMemberRepository.save(preCreatedCardMember);
            log.info("[CardMember][create]: Success");
            return CardMemberCreateResponse.builder()
                    .id(createdCardMember.getId().toString())
                    .cardId(createdCardMember.getCard().getId().toString())
                    .userId(createdCardMember.getUser().getId().toString())
                    .email(createdCardMember.getUser().getEmail())
                    .fullName(createdCardMember.getUser().getFullName())
                    .avatarUrl(createdCardMember.getUser().getAvatarUrl())
                    .build();
        } catch (Exception e) {
            log.error("[CardMember][create]: Error: error=[{}]", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(String id) {
        try {
            log.info("[CardMember][delete]: Start: req=[{}]", id);
            cardMemberRepository.deleteById(UUID.fromString(id));
            log.info("[CardMember][delete]: Success");
        } catch (Exception e) {
            log.error("[CardMember][delete]: Error: error=[{}]", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<CardMemberResponse> findByCardId(String id) {
        try {
            log.info("[CardMember][findByCardId]: Start: req=[{}]", id);
            List<CardMember> members = cardMemberRepository.findByCard_Id(UUID.fromString(id));
            log.info("[CardMember][findByCardId]: Success: length=[{}]", members.size());
            return members.stream().map(m -> (CardMemberResponse.builder()
                    .id(m.getId().toString())
                    .email(m.getUser().getEmail())
                    .fullName(m.getUser().getFullName())
                    .avatarUrl(m.getUser().getAvatarUrl())
                    .userId(m.getUser().getId().toString())
                    .createdAt(m.getCreatedAt() != null ? m.getCreatedAt().toString() : null)
                    .createdBy(m.getCreatedBy() != null ? m.getCreatedBy().toString() : null)
                    .cardId(m.getCard().getId().toString())
                    .build())).toList();
        } catch (Exception e) {
            log.error("[CardMember][findByCardId]: Error: error=[{}]", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


}

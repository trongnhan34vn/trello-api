package com.nhantic.trelloapi.ws;

import com.nhantic.trelloapi.constant.EventType;
import com.nhantic.trelloapi.constant.WsDomain;
import com.nhantic.trelloapi.event.CardCreateEvent;
import com.nhantic.trelloapi.event.CardUpdateEvent;
import com.nhantic.trelloapi.event.WsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CardWsService extends AbstractWsService{
    public CardWsService(SimpMessageSendingOperations messagingTemplate) {
        super(messagingTemplate);
    }

    private static final String TOPIC = "/topic/board.";

    @Override
    protected String getTopic() {
        return TOPIC;
    }

    @Override
    protected String getDomain() {
        return WsDomain.CARD;
    }

    private <T> void broadcastToBoard(String boardId, EventType eventType, T payload) {
        messagingTemplate.convertAndSend(
                getTopic() + boardId,
                WsEvent.of(eventType.toString(), getDomain(), payload)
        );
    }

    public void broadcastCreated(String boardId, CardCreateEvent card) {
        log.info("[Card][broadcast][created] Execute");
        broadcastToBoard(boardId, EventType.CARD_CREATED, card);
    }

    public void broadcastUpdated(String boardId, CardUpdateEvent card) {
        log.info("[Card][broadcast][updated] Execute");
        broadcastToBoard(boardId, EventType.CARD_UPDATED, card);
    }
}

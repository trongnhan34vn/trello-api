package com.nhantic.trelloapi.ws;

import com.nhantic.trelloapi.constant.EventType;
import com.nhantic.trelloapi.constant.WsDomain;
import com.nhantic.trelloapi.event.CardCreateEvent;
import com.nhantic.trelloapi.event.CardUpdateEvent;
import com.nhantic.trelloapi.event.ListCreateEvent;
import com.nhantic.trelloapi.event.WsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ListWsService extends AbstractWsService {
    private static final String TOPIC = "/topic/board.";

    public ListWsService(SimpMessageSendingOperations messagingTemplate) {
        super(messagingTemplate);
    }

    @Override
    protected String getTopic() {
        return TOPIC;
    }

    @Override
    protected String getDomain() {
        return WsDomain.LIST;
    }

    private <T> void broadcastToBoard(String boardId, EventType eventType, T payload) {
        messagingTemplate.convertAndSend(
                getTopic() + boardId,
                WsEvent.of(eventType.toString(), getDomain(), payload)
        );
    }

    public void broadcastCreated(String boardId, ListCreateEvent list) {
        log.info("[List][broadcast][created] Execute");
        broadcastToBoard(boardId, EventType.LIST_CREATED, list);
    }

    public void broadcastUpdated(String boardId, ListCreateEvent list) {
        log.info("[List][broadcast][updated] Execute");
        broadcastToBoard(boardId, EventType.LIST_UPDATED, list);
    }
}

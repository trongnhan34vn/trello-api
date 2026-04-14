package com.nhantic.trelloapi.ws;

import com.nhantic.trelloapi.constant.EventType;
import com.nhantic.trelloapi.constant.WsDomain;
import com.nhantic.trelloapi.event.BoardCreateEvent;
import com.nhantic.trelloapi.event.WsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BoardWsService extends AbstractWsService {
    private static final String TOPIC = "/topic/workspace.";

    public BoardWsService(SimpMessageSendingOperations messagingTemplate) {
        super(messagingTemplate);
    }

    @Override
    protected String getTopic() {
        return TOPIC;
    }

    @Override
    protected String getDomain() {
        return WsDomain.BOARD;
    }

    private <T> void broadcastToBoard(String boardId, EventType eventType, T payload) {
        messagingTemplate.convertAndSend(
                getTopic() + boardId,
                WsEvent.of(eventType.toString(), getDomain(), payload)
        );
    }

    public void broadcastCreated(String workspaceId, BoardCreateEvent board) {
        log.info("[Board][broadcast][created] Execute");
        broadcastToBoard(workspaceId, EventType.BOARD_CREATED, board);
    }
}

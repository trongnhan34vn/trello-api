package com.nhantic.trelloapi.ws;

import com.nhantic.trelloapi.event.WsEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

@RequiredArgsConstructor
public abstract class AbstractWsService {
    protected final SimpMessageSendingOperations messagingTemplate;
    protected abstract String getTopic();
    protected abstract String getDomain();

    protected <T> void broadcast(String eventType, T payload) {
        WsEvent<T> event = new WsEvent<>(eventType, getDomain(), payload);
        messagingTemplate.convertAndSend(getTopic(), event);
    }

    public  <T> void sendToUser(String userId, String eventType, T payload) {
        WsEvent<T> event = new WsEvent<>(eventType, getDomain(), payload);
        String destination = "/queue/" + getDomain().toLowerCase();
        messagingTemplate.convertAndSendToUser(userId, destination, event);
    }

}

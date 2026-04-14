package com.nhantic.trelloapi.event;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class WsEvent<T> {
    private T payload;
    private String domain;
    private String eventType;
    private LocalDateTime timestamp = LocalDateTime.now();

    public WsEvent(String type, String domain, T payload) {
        this.payload = payload;
        this.domain = domain;
        this.eventType = type;
    }

    public static <T> WsEvent<T> of(String eventType, String domain, T payload) {
        WsEvent<T> event = new WsEvent<>();
        event.eventType = eventType;
        event.domain = domain;
        event.payload = payload;
        event.timestamp = LocalDateTime.now();
        return event;
    }
}

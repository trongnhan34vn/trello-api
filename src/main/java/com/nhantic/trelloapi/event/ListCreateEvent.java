package com.nhantic.trelloapi.event;

import lombok.*;

@Builder
@Getter
@ToString
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListCreateEvent {
    private String id;
    private String name;
    private String boardId;
    private String position;
}

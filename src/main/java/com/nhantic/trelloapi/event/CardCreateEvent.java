package com.nhantic.trelloapi.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardCreateEvent {
    private String id;
    private String title;
    private int position;
    private String listId;
    private String createdBy;
    private boolean isInbox;
    private String boardId;
}

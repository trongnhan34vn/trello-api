package com.nhantic.trelloapi.event;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CardUpdateEvent {
    private String id;
    private String title;
    private String fromListId;
    private String toListId;
    private int position;
    private String updatedBy;
    private LocalDateTime timestamp;
    private boolean isInbox;
}

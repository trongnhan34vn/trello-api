package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CardCreateResponse {
    private String id;
    private boolean isInbox;
    private String createdAt;
    private String listId;
    private String title;
    private String createdBy;
    private String boardId;
    private int position;
}

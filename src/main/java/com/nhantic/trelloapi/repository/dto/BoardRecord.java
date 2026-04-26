package com.nhantic.trelloapi.repository.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardWithListAndCard {
    private UUID boardId;
    private String boardName;
    private String boardBackgroundUrl;
    private UUID listId;
    private String listName;
    private UUID cardId;
    private String cardName;
}

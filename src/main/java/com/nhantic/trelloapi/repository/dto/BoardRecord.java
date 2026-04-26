package com.nhantic.trelloapi.repository.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardRecord {
    // Board
    private UUID boardId;
    private String boardName;
    private String boardBackgroundUrl;

    // Row type
    private String rowType;

    // List + Card fields (null khi rowType = 'MEMBER')
    private UUID listId;
    private String listName;
    private UUID cardId;
    private String cardTitle;

    // Member fields (null khi rowType = 'LIST')
    private UUID memberId;
    private String memberFullName;
    private String memberEmail;
    private String memberAvatar;
    private Integer memberRoleId;
}

package com.nhantic.trelloapi.repository.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WorkspaceWithBoard {
    private UUID workspaceId;
    private String workspaceName;
    private String description;

    private UUID boardId;
    private String boardName;
    private String backgroundUrl;
}

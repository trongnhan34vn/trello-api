package com.nhantic.trelloapi.repository.dto;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceRecord {
    // Workspace
    private UUID workspaceId;
    private String workspaceName;
    private String description;

    // Row type discriminator
    private String rowType;

    // Board fields (null khi rowType = 'MEMBER')
    private UUID boardId;
    private String boardName;
    private String backgroundUrl;

    // Member fields (null khi rowType = 'BOARD')
    private UUID memberId;
    private String memberFullName;
    private String memberEmail;
    private String memberAvatar;
    private Integer memberRoleId;
    private Timestamp createdAt;
}

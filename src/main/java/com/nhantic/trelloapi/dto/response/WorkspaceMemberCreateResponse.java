package com.nhantic.trelloapi.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class WorkspaceMemberCreateResponse {
    private String id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private int roleId;
    private String userId;
    private String workspaceId;
}

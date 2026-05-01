package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BoardMemberCreateResponse {
    private String id;
    private String fullName;
    private String email;
    private String avatarUrl;
    private int roleId;
    private String boardId;
    private String userId;
}

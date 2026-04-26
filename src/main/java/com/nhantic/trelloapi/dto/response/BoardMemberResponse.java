package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardMemberResponse {
    private String id;
    private String fullName;
    private String email;
    private String avatarUrl;
    private int roleId;
}

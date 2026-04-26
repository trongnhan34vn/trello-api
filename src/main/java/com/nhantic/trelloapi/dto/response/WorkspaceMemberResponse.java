package com.nhantic.trelloapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkspaceMemberResponse {
    private String id;
    private String fullName;
    private String email;
    private String avatarUrl;
    private int roleId;
}

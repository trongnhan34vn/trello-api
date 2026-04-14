package com.nhantic.trelloapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateResponse {
    private String fullName;
    private String email;
    private String avatarUrl;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
}

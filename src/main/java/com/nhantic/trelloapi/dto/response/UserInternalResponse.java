package com.nhantic.trelloapi.dto.response;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInternalResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String cognitoId;
    private String createdAt;
    private String updatedAt;
}

package com.nhantic.trelloapi.dto.request;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    private String email;
    private String fullName;
    private String cognitoId;
}

package com.nhantic.trelloapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CognitoSignUpRequest {
    private String username;
    private String password;
    private String fullName;
}

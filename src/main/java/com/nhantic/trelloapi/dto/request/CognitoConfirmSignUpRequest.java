package com.nhantic.trelloapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CognitoConfirmSignUpRequest {
    private String username;
    private String confirmationCode;
}

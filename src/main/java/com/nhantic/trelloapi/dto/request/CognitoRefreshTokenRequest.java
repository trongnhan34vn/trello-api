package com.nhantic.trelloapi.dto.request;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CognitoRefreshTokenRequest {
    private String token;
    private String username;
}

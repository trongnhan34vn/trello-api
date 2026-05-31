package com.nhantic.trelloapi.dto.request;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RefreshTokenRequest {
    private String token;
}

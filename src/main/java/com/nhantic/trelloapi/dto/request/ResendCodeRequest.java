package com.nhantic.trelloapi.dto.request;

import lombok.*;

@Builder
@Getter
@ToString
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResendCodeRequest {
    private String username;
}

package com.nhantic.trelloapi.dto.request;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {
    private String newPassword;
    private String oldPassword;
    private String accessToken;
}

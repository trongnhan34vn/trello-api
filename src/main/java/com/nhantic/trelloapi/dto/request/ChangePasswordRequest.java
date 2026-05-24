package com.nhantic.trelloapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank
    private String newPassword;
    @NotBlank
    private String currentPassword;
    private String accessToken;
}

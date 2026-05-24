package com.nhantic.trelloapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
    private String fullName;
    private String avatarUrl;
    private String bio;
    private String phone;
    private String address;
    private String cognitoId;
}

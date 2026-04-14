package com.nhantic.trelloapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardCreateRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String workspaceId;
    private String backgroundUrl;

    private String createdBy;
}

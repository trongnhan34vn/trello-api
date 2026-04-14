package com.nhantic.trelloapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkspaceCreateRequest {
    @NotBlank
    private String name;
    @NotNull
    private Integer categoryId;
    private String description;
    private String createdBy;
}

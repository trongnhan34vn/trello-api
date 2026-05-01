package com.nhantic.trelloapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CardCreateRequest {
    @NotBlank
    private String title;
    @NotNull
    private String listId;
    private String createdBy;
    @NotNull
    private String position;
}

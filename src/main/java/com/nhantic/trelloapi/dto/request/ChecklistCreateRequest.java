package com.nhantic.trelloapi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ChecklistCreateRequest {
    @NotNull
    private String name;
    @NotNull
    private String cardId;
    @NotNull
    private String position;

    private String createdBy;
}

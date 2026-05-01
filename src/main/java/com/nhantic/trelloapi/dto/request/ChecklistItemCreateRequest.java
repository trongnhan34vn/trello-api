package com.nhantic.trelloapi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ChecklistItemCreateRequest {
    @NotNull
    private String name;
    @NotNull
    private String checklistId;
    @NotNull
    private String position;

    private String dueDate;
    private String createdBy;
}

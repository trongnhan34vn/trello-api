package com.nhantic.trelloapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChecklistItemUpdateRequest {
    private String id;
    private String name;
    private String checklistId;
    private String position;
    @JsonProperty("isCompleted")
    private boolean isCompleted;
    private String dueDate;
    private String updatedBy;
}

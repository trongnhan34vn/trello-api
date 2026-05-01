package com.nhantic.trelloapi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChecklistItemResponse {
    private String id;
    private String name;
    private String checklistId;
    private String position;
    @JsonProperty("isCompleted")
    private boolean isCompleted;
    private String dueDate;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;
}

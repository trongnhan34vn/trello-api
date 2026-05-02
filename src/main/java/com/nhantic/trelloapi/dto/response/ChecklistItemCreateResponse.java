package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChecklistItemCreateResponse {
    private String id;
    private String name;
    private String position;
    private String checklistId;
    private boolean isCompleted;
    private String dueDate;
}

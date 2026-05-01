package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistItemUpdateResponse {
    private String id;
    private String name;
    private String position;
    private String dueDate;
    private String checklistId;
    private String updateBy;
    private String updatedAt;
    private String createdBy;
    private String createdAt;
}

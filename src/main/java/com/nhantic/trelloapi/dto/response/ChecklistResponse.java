package com.nhantic.trelloapi.dto.response;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ChecklistResponse {
    private String id;
    private String name;
    private String cardId;
    private String position;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;
    private List<ChecklistItemResponse> checklistItems;
}

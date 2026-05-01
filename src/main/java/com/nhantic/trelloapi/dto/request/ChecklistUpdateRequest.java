package com.nhantic.trelloapi.dto.request;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ChecklistUpdateRequest {
    private String id;
    private String name;
    private String cardId;
    private String position;
    private String updatedBy;
}

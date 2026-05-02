package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChecklistUpdateResponse {
    private String id;
    private String name;
    private String cardId;
    private String position;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;
}

package com.nhantic.trelloapi.dto.request;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CardUpdateRequest {
    private String id;
    private String title;
    private String description;
    private String startDate;
    private String dueDate;
    private int position;
    private String listId;
    private String updatedBy;
}

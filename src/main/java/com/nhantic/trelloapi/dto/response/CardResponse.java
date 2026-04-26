package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {
    private String id;
    private String title;
    private String description;
    private String startDate;
    private String dueDate;
    private int position;
    private String listId;
    private String updatedBy;
}

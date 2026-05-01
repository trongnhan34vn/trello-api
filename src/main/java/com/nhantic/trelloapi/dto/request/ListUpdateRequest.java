package com.nhantic.trelloapi.dto.request;

import lombok.*;

@Builder
@Getter
@ToString
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListUpdateRequest {
    private String id;
    private String name;
    private String position;
    private String updatedBy;
}

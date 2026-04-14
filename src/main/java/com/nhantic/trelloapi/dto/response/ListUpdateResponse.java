package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListUpdateResponse {
    private String id;
    private String name;
    private int position;
    private String updatedBy;
}

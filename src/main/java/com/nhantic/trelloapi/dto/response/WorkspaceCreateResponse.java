package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WorkspaceCreateResponse {
    private String id;
    private String name;
    private String description;
    private String createdBy;
}

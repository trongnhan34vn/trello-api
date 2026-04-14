package com.nhantic.trelloapi.dto.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceResponse {
    private String id;
    private String name;
    private String description;
    private List<BoardResponse> boards;
}

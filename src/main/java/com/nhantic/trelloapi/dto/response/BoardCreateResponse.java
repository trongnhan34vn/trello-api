package com.nhantic.trelloapi.dto.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardCreateResponse {
    private String id;
    private String name;
    private String backgroundUrl;
    private String workspaceId;
    private List<String> members;
}

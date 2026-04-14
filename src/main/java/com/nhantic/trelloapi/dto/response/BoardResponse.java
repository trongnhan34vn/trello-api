package com.nhantic.trelloapi.dto.response;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BoardResponse {
    private String id;
    private String name;
    private String backgroundUrl;
    private List<ListResponse> lists;
}

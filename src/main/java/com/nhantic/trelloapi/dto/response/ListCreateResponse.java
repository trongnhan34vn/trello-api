package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@Getter
@ToString
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListCreateResponse {
    private String id;
    private String name;
    private int position;
    private String boardId;
    private String createdAt;
    private String createdBy;
}

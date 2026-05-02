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
    private String position;
    private String boardId;
    private String createdBy;
}

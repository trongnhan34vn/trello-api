package com.nhantic.trelloapi.dto.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListResponse {
    private String id;
    private String name;
    private int position;
    private List<CardResponse> cards;
    private String boardId;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;
}

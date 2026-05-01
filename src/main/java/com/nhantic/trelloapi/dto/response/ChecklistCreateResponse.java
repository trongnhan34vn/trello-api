package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChecklistCreateResponse {
    private String id;
    private String name;
    private String cardId;
    private String position;
}

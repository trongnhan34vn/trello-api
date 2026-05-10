package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CardMemberCreateResponse {
    private String id;
    private String userId;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String cardId;
}

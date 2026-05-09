package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CardMemberResponse {
    private String id;
    private String fullName;
    private String email;
    private String avatarUrl;
    private String createdBy;
    private String createdAt;
    private String userId;
    private String cardId;
}

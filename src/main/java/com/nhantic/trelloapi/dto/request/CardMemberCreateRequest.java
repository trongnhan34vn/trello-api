package com.nhantic.trelloapi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardMemberCreateRequest {
    @NotNull
    private String cardId;
    @NotNull
    private String userId;
    private String createdBy;
}

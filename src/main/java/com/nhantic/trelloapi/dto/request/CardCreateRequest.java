package com.nhantic.trelloapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CardCreateRequest {
    @NotBlank
    private String title;
    private String listId;
    private String createdBy;
    private int position;
    private boolean isInbox;
}

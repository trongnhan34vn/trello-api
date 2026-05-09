package com.nhantic.trelloapi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BoardMemberCreateRequest {
    @NotNull
    private List<String> userIds;
    @NotNull
    private String boardId;
    @NotNull
    @Builder.Default
    private int roleId = 2;

    private String createdBy;
}

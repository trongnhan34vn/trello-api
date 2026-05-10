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
public class WorkspaceMemberCreateRequest {
    @NotNull
    private String workspaceId;
    @NotNull
    private List<String> userIds;
    @Builder.Default
    private int roleId = 2;
    private String createdBy;
}

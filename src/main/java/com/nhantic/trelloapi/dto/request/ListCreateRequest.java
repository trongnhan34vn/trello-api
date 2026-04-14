package com.nhantic.trelloapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ListCreateRequest {
    @NotBlank
    private String name;
    @NotNull
    private int position;
    @NotNull
    private String boardId;
    private String createdBy;
}

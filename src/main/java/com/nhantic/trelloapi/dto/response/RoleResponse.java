package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private int id;
    private String name;
}

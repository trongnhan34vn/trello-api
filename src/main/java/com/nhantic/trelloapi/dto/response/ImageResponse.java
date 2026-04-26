package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private int id;
    private String url;
}

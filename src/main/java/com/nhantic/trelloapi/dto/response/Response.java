package com.nhantic.trelloapi.dto.response;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private boolean success;
    private String message;
    private String code;
    private Object data;
    private Object cause;
}

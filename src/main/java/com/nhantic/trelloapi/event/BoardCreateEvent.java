package com.nhantic.trelloapi.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardCreateEvent {
    private String id;
    private String name;
    private String backgroundUrl;
    private String workspaceId;
    private LocalDateTime timestamp;
    private List<String> members;
}

package com.example.preppilot.demo.dto.response;

import com.example.preppilot.demo.entity.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {
    private Long id;
    private String title;
    private ResourceType type;
    private String url;
    private LocalDateTime createdAt;
}

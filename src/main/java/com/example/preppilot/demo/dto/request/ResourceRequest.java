package com.example.preppilot.demo.dto.request;

import com.example.preppilot.demo.entity.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResourceRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Type is required")
    private ResourceType type;

    // for LINK type — user sends the URL directly
    // for PDF type — this will be filled by the service after file upload
    private String url;
}

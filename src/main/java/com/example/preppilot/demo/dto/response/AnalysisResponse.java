package com.example.preppilot.demo.dto.response;

import com.example.preppilot.demo.entity.AnalysisMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResponse {
    private Long id;
    private AnalysisMode mode;
    private String result;
    private String companyName;
    private LocalDateTime createdAt;
}

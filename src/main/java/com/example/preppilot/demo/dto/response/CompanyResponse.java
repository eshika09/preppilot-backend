package com.example.preppilot.demo.dto.response;

import com.example.preppilot.demo.entity.CompanyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {
    private Long id;
    private String name;
    private String role;
    private CompanyStatus status;
    private String notes;
    private LocalDate deadline;
    private LocalDateTime createdAt;
}

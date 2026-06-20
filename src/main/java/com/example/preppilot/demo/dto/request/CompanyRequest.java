package com.example.preppilot.demo.dto.request;

import com.example.preppilot.demo.entity.CompanyStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CompanyRequest {
    @NotBlank(message = "Company name is required")
    private String name;

    @NotBlank(message = "Role is required")
    private String role;

    private CompanyStatus status;

    private String notes;

    private LocalDate deadline;
}

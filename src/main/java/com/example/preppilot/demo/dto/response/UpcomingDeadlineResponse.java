package com.example.preppilot.demo.dto.response;

import com.example.preppilot.demo.entity.CompanyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingDeadlineResponse {
    private Long companyId;
    private String companyName;
    private String role;
    private LocalDate deadline;
    private CompanyStatus status;
    private long daysLeft;
}

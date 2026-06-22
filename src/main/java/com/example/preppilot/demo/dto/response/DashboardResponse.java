package com.example.preppilot.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long totalCompanies;
    private Map<String, Long> companiesByStatus;   // { "APPLIED": 3, "INTERVIEW": 1 }
    private Map<String, Long> companiesByPriority; // { "HIGH": 2, "MEDIUM": 3 }
    private long totalResources;
    private List<UpcomingDeadlineResponse> upcomingDeadlines;
    private int analysesUsed;
    private int analysesRemaining;  // 3 for free users, -1 means unlimited (premium)

}

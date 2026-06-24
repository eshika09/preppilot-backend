package com.example.preppilot.demo.dto.response;

import com.example.preppilot.demo.entity.ExperienceOutcome;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityExperienceResponse {
    private Long id;
    private String companyName;
    private String role;
    private String round;
    private String questionsAsked;
    private String tips;
    private ExperienceOutcome outcome;
    private String postedBy;
    private LocalDateTime createdAt;
}

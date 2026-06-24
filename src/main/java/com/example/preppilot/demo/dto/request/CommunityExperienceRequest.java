package com.example.preppilot.demo.dto.request;

import com.example.preppilot.demo.entity.ExperienceOutcome;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommunityExperienceRequest {
    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Role is required")
    private String role;

    @NotBlank(message = "Round is required")
    private String round;

    @NotBlank(message = "Questions asked is required")
    private String questionsAsked;

    private String tips;

    @NotNull(message = "Outcome is required")
    private ExperienceOutcome outcome;
}

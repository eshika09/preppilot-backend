package com.example.preppilot.demo.controller;

import com.example.preppilot.demo.dto.request.CommunityExperienceRequest;
import com.example.preppilot.demo.dto.response.CommunityExperienceResponse;
import com.example.preppilot.demo.service.CommunityExperienceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityExperienceController {
    private final CommunityExperienceService experienceService;

    // Post experience — login required
    @PostMapping
    public ResponseEntity<CommunityExperienceResponse> postExperience(
            @Valid @RequestBody CommunityExperienceRequest request) {
        return ResponseEntity.ok(experienceService.postExperience(request));
    }

    // Get all experiences — public
    @GetMapping
    public ResponseEntity<List<CommunityExperienceResponse>> getAllExperiences() {
        return ResponseEntity.ok(experienceService.getAllExperiences());
    }

    // Get by company name — public
    @GetMapping("/company/{companyName}")
    public ResponseEntity<List<CommunityExperienceResponse>> getByCompany(
            @PathVariable String companyName) {
        return ResponseEntity.ok(experienceService.getExperiencesByCompany(companyName));
    }
}

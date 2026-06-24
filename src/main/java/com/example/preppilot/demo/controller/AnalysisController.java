package com.example.preppilot.demo.controller;

import com.example.preppilot.demo.dto.response.AnalysisResponse;
import com.example.preppilot.demo.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalysisService analysisService;

    // Analyze OA paper PDF
    @PostMapping("/oa-paper/{companyId}")
    public ResponseEntity<AnalysisResponse> analyzeOaPaper(
            @PathVariable Long companyId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(analysisService.analyzeOaPaper(companyId, file));
    }

    // Analyze Job Description PDF
    @PostMapping("/job-description/{companyId}")
    public ResponseEntity<AnalysisResponse> analyzeJobDescription(
            @PathVariable Long companyId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(analysisService.analyzeJobDescription(companyId, file));
    }

    // Generate status-based prep plan
    @PostMapping("/prep-plan/{companyId}")
    public ResponseEntity<AnalysisResponse> generatePrepPlan(
            @PathVariable Long companyId) {
        return ResponseEntity.ok(analysisService.generatePrepPlan(companyId));
    }

    // Get all analyses for a company
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<AnalysisResponse>> getAnalysesForCompany(
            @PathVariable Long companyId) {
        return ResponseEntity.ok(analysisService.getAnalysesForCompany(companyId));
    }

}

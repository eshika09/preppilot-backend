package com.example.preppilot.demo.service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.example.preppilot.demo.Repository.AnalysisRepository;
import com.example.preppilot.demo.Repository.CommunityExperienceRepository;
import com.example.preppilot.demo.Repository.CompanyRepository;
import com.example.preppilot.demo.Repository.UserRepository;
import com.example.preppilot.demo.dto.response.AnalysisResponse;
import com.example.preppilot.demo.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private final AnalysisRepository analysisRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CommunityExperienceRepository communityExperienceRepository;
    private final GeminiService geminiService;

    private static final int FREE_ANALYSIS_LIMIT = 3;

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private void checkAnalysisLimit(User user) {
        if (!user.isPremium() && user.getAnalysisCount() >= FREE_ANALYSIS_LIMIT) {
            throw new RuntimeException(
                    "Free limit reached. You have used " + FREE_ANALYSIS_LIMIT +
                            " analyses. Upgrade to premium for unlimited analyses.");
        }
    }

    private void incrementAnalysisCount(User user) {
        user.setAnalysisCount(user.getAnalysisCount() + 1);
        userRepository.save(user);
    }

    private AnalysisResponse toResponse(Analysis analysis) {
        return AnalysisResponse.builder()
                .id(analysis.getId())
                .mode(analysis.getMode())
                .result(analysis.getResult())
                .companyName(analysis.getCompany().getName())
                .createdAt(analysis.getCreatedAt())
                .build();
    }

    // OA Paper Analysis
    @Transactional
    public AnalysisResponse analyzeOaPaper(Long companyId,
                                           MultipartFile file) throws IOException {
        User user = getLoggedInUser();
        checkAnalysisLimit(user);

        Company company = companyRepository.findByIdAndUser(companyId, user)
                .orElseThrow(() -> new RuntimeException("Company not found or access denied"));

        String pdfText = geminiService.extractTextFromPdf(file);
        if (pdfText.isBlank()) {
            throw new RuntimeException("Could not extract text from PDF. Make sure it's not a scanned image.");
        }

        String prompt = geminiService.buildOaPaperPrompt(pdfText);
        String result = geminiService.callGemini(prompt);

        Analysis analysis = Analysis.builder()
                .mode(AnalysisMode.OA_PAPER)
                .result(result)
                .user(user)
                .company(company)
                .build();

        analysisRepository.save(analysis);
        incrementAnalysisCount(user);

        return toResponse(analysis);
    }

    // JD Analysis
    @Transactional
    public AnalysisResponse analyzeJobDescription(Long companyId,
                                                  MultipartFile file) throws IOException {
        User user = getLoggedInUser();
        checkAnalysisLimit(user);

        Company company = companyRepository.findByIdAndUser(companyId, user)
                .orElseThrow(() -> new RuntimeException("Company not found or access denied"));

        String pdfText = geminiService.extractTextFromPdf(file);
        if (pdfText.isBlank()) {
            throw new RuntimeException("Could not extract text from PDF.");
        }

        String prompt = geminiService.buildJdPrompt(pdfText);
        String result = geminiService.callGemini(prompt);

        Analysis analysis = Analysis.builder()
                .mode(AnalysisMode.JOB_DESCRIPTION)
                .result(result)
                .user(user)
                .company(company)
                .build();

        analysisRepository.save(analysis);
        incrementAnalysisCount(user);

        return toResponse(analysis);
    }

    // Status-based Prep Plan
    @Transactional
    public AnalysisResponse generatePrepPlan(Long companyId) {
        User user = getLoggedInUser();
        checkAnalysisLimit(user);

        Company company = companyRepository.findByIdAndUser(companyId, user)
                .orElseThrow(() -> new RuntimeException("Company not found or access denied"));

        // block if status doesn't need prep
        if (company.getStatus() == CompanyStatus.SELECTED ||
                company.getStatus() == CompanyStatus.REJECTED) {
            throw new RuntimeException(
                    "Prep plan is only available when actively preparing. Current status: "
                            + company.getStatus());
        }

        // fetch community experiences for this company
        List<CommunityExperience> experiences = communityExperienceRepository
                .findByCompanyNameIgnoreCaseOrderByCreatedAtDesc(company.getName());

        // format experiences into readable text for Gemini
        String experiencesText;
        if (experiences.isEmpty()) {
            experiencesText = "No community experiences available yet for this company. " +
                    "Please provide general preparation advice based on the status.";
        } else {
            experiencesText = experiences.stream()
                    .map(e -> String.format(
                            "Round: %s | Role: %s | Outcome: %s\nQuestions: %s\nTips: %s",
                            e.getRound(), e.getRole(), e.getOutcome(),
                            e.getQuestionsAsked(),
                            e.getTips() != null ? e.getTips() : "No tips provided"))
                    .collect(Collectors.joining("\n\n---\n\n"));
        }

        String prompt = geminiService.buildPrepPlanPrompt(
                company.getName(),
                company.getStatus().name(),
                company.getRole(),
                experiencesText);

        String result = geminiService.callGemini(prompt);

        Analysis analysis = Analysis.builder()
                .mode(AnalysisMode.PREP_PLAN)
                .result(result)
                .user(user)
                .company(company)
                .build();

        analysisRepository.save(analysis);
        incrementAnalysisCount(user);

        return toResponse(analysis);
    }

    // Get all analyses for a company
    public List<AnalysisResponse> getAnalysesForCompany(Long companyId) {
        User user = getLoggedInUser();
        Company company = companyRepository.findByIdAndUser(companyId, user)
                .orElseThrow(() -> new RuntimeException("Company not found or access denied"));
        return analysisRepository.findByUserAndCompanyOrderByCreatedAtDesc(user, company)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}

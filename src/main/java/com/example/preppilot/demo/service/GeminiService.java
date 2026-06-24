package com.example.preppilot.demo.service;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {
    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    // extract text from PDF using PDFBox
    public String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    // call Gemini API with a prompt
    public String callGemini(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            Map body = response.getBody();
            List choices = (List) body.get("choices");
            Map choice = (Map) choices.get(0);
            Map message = (Map) choice.get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AI API call failed: " + e.getMessage());
        }
    }

    // Prompt 1 — OA Paper analysis
    public String buildOaPaperPrompt(String pdfText) {
        return """
                You are a placement preparation expert. Analyze this Online Assessment (OA) paper and provide:
                
                1. TOPICS COVERED: List all DSA/technical topics found
                2. DIFFICULTY LEVEL: Easy / Medium / Hard with reasoning
                3. REVISION SUGGESTIONS: What to study before attempting similar OAs
                4. TIME ESTIMATE: How long to prepare each topic
                
                OA Paper Content:
                """ + pdfText;
    }

    // Prompt 2 — Job Description analysis
    public String buildJdPrompt(String pdfText) {
        return """
                You are a placement preparation expert. Analyze this Job Description and provide:
                
                1. KEY SKILLS REQUIRED: Technical and soft skills
                2. RESUME TIPS: What to highlight, add, or remove for this specific role
                3. INTERVIEW FOCUS AREAS: What topics to prepare based on this JD
                4. RED FLAGS: Any requirements that need immediate attention
                
                Job Description:
                """ + pdfText;
    }

    // Prompt 3 — Status-based Prep Plan using community experiences
    public String buildPrepPlanPrompt(String companyName,
                                      String currentStatus,
                                      String role,
                                      String communityExperiences) {
        String focusArea = switch (currentStatus) {
            case "APPLIED", "OA_SCHEDULED" -> "ONLINE ASSESSMENT (OA)";
            case "OA_CLEARED", "INTERVIEW" -> "INTERVIEW ROUNDS";
            default -> "GENERAL PREPARATION";
        };

        return String.format("""
                You are a placement preparation expert. A student is preparing for %s at %s for the role of %s.
                Their current application status is: %s
                The immediate focus should be: %s
                
                Based on these REAL community experiences from people who interviewed at %s:
                
                %s
                
                Provide a detailed preparation plan:
                
                1. WHAT TO FOCUS ON: Specific topics based on real experiences above
                2. DAY-WISE PLAN: A realistic 7-day preparation schedule
                3. KEY TIPS: Directly from the community experiences
                4. RESOURCES TO USE: Types of resources recommended
                5. WHAT TO AVOID: Common mistakes mentioned in experiences
                
                Be specific, practical, and base your advice on the real experiences provided.
                """,
                focusArea, companyName, role, currentStatus, focusArea,
                companyName, communityExperiences);
    }

}

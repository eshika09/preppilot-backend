package com.example.preppilot.demo.service;

import com.example.preppilot.demo.Repository.CommunityExperienceRepository;
import com.example.preppilot.demo.Repository.UserRepository;
import com.example.preppilot.demo.dto.request.CommunityExperienceRequest;
import com.example.preppilot.demo.dto.response.CommunityExperienceResponse;
import com.example.preppilot.demo.entity.CommunityExperience;
import com.example.preppilot.demo.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityExperienceService {
    private final CommunityExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private CommunityExperienceResponse toResponse(CommunityExperience e) {
        return CommunityExperienceResponse.builder()
                .id(e.getId())
                .companyName(e.getCompanyName())
                .role(e.getRole())
                .round(e.getRound())
                .questionsAsked(e.getQuestionsAsked())
                .tips(e.getTips())
                .outcome(e.getOutcome())
                .postedBy(e.getUser().getName())
                .createdAt(e.getCreatedAt())
                .build();
    }

    // Post an experience — login required
    @Transactional
    public CommunityExperienceResponse postExperience(CommunityExperienceRequest request) {
        User user = getLoggedInUser();

        CommunityExperience experience = CommunityExperience.builder()
                .companyName(request.getCompanyName())
                .role(request.getRole())
                .round(request.getRound())
                .questionsAsked(request.getQuestionsAsked())
                .tips(request.getTips())
                .outcome(request.getOutcome())
                .user(user)
                .build();

        return toResponse(experienceRepository.save(experience));
    }

    // Get all experiences — no login required (public feed)
    public List<CommunityExperienceResponse> getAllExperiences() {
        return experienceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get experiences by company name
    public List<CommunityExperienceResponse> getExperiencesByCompany(String companyName) {
        return experienceRepository
                .findByCompanyNameIgnoreCaseOrderByCreatedAtDesc(companyName)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}

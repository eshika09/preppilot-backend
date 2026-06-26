package com.example.preppilot.demo.service;

import com.example.preppilot.demo.Repository.CompanyRepository;
import com.example.preppilot.demo.Repository.UserRepository;
import com.example.preppilot.demo.dto.request.CompanyRequest;
import com.example.preppilot.demo.dto.response.CompanyResponse;
import com.example.preppilot.demo.entity.Company;
import com.example.preppilot.demo.entity.CompanyPriority;
import com.example.preppilot.demo.entity.CompanyStatus;
import com.example.preppilot.demo.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    // helper — gets logged in user from SecurityContext
    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // helper — converts Company entity to CompanyResponse DTO
    private CompanyResponse toResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .role(company.getRole())
                .status(company.getStatus())
                .notes(company.getNotes())
                .deadline(company.getDeadline())
                .createdAt(company.getCreatedAt())
                .priority(company.getPriority())
                .build();
    }

    @Transactional
    public CompanyResponse addCompany(CompanyRequest request) {
        User user = getLoggedInUser();

        Company company = Company.builder()
                .name(request.getName())
                .role(request.getRole())
                .status(request.getStatus() != null ? request.getStatus() : CompanyStatus.APPLIED)
                .priority(request.getPriority() != null ? request.getPriority() : CompanyPriority.MEDIUM)
                .notes(request.getNotes())
                .deadline(request.getDeadline())
                .user(user)
                .build();

        return toResponse(companyRepository.save(company));
    }

    public Map<String, Object> getAllCompanies(int page, int size) {
        User user = getLoggedInUser();
        Pageable pageable = PageRequest.of(page, size);
        Page<Company> companyPage = companyRepository
                .findByUserOrderByCreatedAtDesc(user, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("companies", companyPage.getContent()
                .stream().map(this::toResponse).collect(Collectors.toList()));
        response.put("currentPage", companyPage.getNumber());
        response.put("totalItems", companyPage.getTotalElements());
        response.put("totalPages", companyPage.getTotalPages());
        response.put("isLast", companyPage.isLast());

        return response;
    }

    public CompanyResponse getCompany(Long id) {
        User user = getLoggedInUser();
        Company company = companyRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Company not found or access denied"));
        return toResponse(company);
    }

    @Transactional
    public CompanyResponse updateCompany(Long id, CompanyRequest request) {
        User user = getLoggedInUser();
        Company company = companyRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Company not found or access denied"));

        company.setName(request.getName());
        company.setRole(request.getRole());
        if (request.getStatus() != null) {
            company.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            company.setPriority(request.getPriority());
        }
        company.setNotes(request.getNotes());
        company.setDeadline(request.getDeadline());

        return toResponse(companyRepository.save(company));
    }

    @Transactional
    public void deleteCompany(Long id) {
        User user = getLoggedInUser();
        Company company = companyRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Company not found or access denied"));
        companyRepository.delete(company);
    }

    // Search companies by name
    public List<CompanyResponse> searchCompanies(String name) {
        User user = getLoggedInUser();
        return companyRepository
                .findByUserAndNameContainingIgnoreCaseOrderByCreatedAtDesc(user, name)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Filter companies by status and/or priority
    public List<CompanyResponse> filterCompanies(CompanyStatus status, CompanyPriority priority) {
        User user = getLoggedInUser();
        List<Company> results;

        if (status != null && priority != null) {
            results = companyRepository
                    .findByUserAndStatusAndPriorityOrderByCreatedAtDesc(user, status, priority);
        } else if (status != null) {
            results = companyRepository
                    .findByUserAndStatusOrderByCreatedAtDesc(user, status);
        } else if (priority != null) {
            results = companyRepository
                    .findByUserAndPriorityOrderByCreatedAtDesc(user, priority);
        } else {
            results = companyRepository.findByUserOrderByCreatedAtDesc(user);
        }

        return results.stream().map(this::toResponse).collect(Collectors.toList());
    }
}

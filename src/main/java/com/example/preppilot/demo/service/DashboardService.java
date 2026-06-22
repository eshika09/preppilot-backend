package com.example.preppilot.demo.service;

import com.example.preppilot.demo.Repository.CompanyRepository;
import com.example.preppilot.demo.Repository.ResourceRepository;
import com.example.preppilot.demo.Repository.UserRepository;
import com.example.preppilot.demo.dto.response.DashboardResponse;
import com.example.preppilot.demo.dto.response.UpcomingDeadlineResponse;
import com.example.preppilot.demo.entity.Company;
import com.example.preppilot.demo.entity.CompanyPriority;
import com.example.preppilot.demo.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final CompanyRepository companyRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public DashboardResponse getDashboard() {
        User user = getLoggedInUser();
        LocalDate today = LocalDate.now();

        // --- companies by status ---
        List<Object[]> statusCounts = companyRepository.countByStatusForUser(user);
        Map<String, Long> companiesByStatus = new HashMap<>();
        for (Object[] row : statusCounts) {
            companiesByStatus.put(row[0].toString(), (Long) row[1]);
        }

        // --- total companies ---
        long totalCompanies = companiesByStatus.values().stream().mapToLong(Long::longValue).sum();

        // --- companies by priority ---
        List<Company> allCompanies = companyRepository.findByUserOrderByCreatedAtDesc(user);
        Map<String, Long> companiesByPriority = allCompanies.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getPriority() != null ? c.getPriority().name() : CompanyPriority.MEDIUM.name(),
                        Collectors.counting()
                ));

        // --- upcoming deadlines (next 7 days) ---
        List<UpcomingDeadlineResponse> upcomingDeadlines = companyRepository
                .findByUserAndDeadlineBetweenOrderByDeadlineAsc(user, today, today.plusDays(7))
                .stream()
                .map(c -> UpcomingDeadlineResponse.builder()
                        .companyId(c.getId())
                        .companyName(c.getName())
                        .role(c.getRole())
                        .deadline(c.getDeadline())
                        .status(c.getStatus())
                        .daysLeft(ChronoUnit.DAYS.between(today, c.getDeadline()))
                        .build())
                .collect(Collectors.toList());

        // --- total resources ---
        long totalResources = resourceRepository.findByUserOrderByCreatedAtDesc(user).size();

        // --- analyses used / remaining ---
        int analysesUsed = user.getAnalysisCount();
        int analysesRemaining = user.isPremium() ? -1 : Math.max(0, 3 - analysesUsed);

        return DashboardResponse.builder()
                .totalCompanies(totalCompanies)
                .companiesByStatus(companiesByStatus)
                .companiesByPriority(companiesByPriority)
                .totalResources(totalResources)
                .upcomingDeadlines(upcomingDeadlines)
                .analysesUsed(analysesUsed)
                .analysesRemaining(analysesRemaining)
                .build();
    }
}

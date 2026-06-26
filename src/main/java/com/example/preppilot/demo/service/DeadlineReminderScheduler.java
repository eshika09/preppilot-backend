package com.example.preppilot.demo.service;

import com.example.preppilot.demo.Repository.CompanyRepository;
import com.example.preppilot.demo.entity.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeadlineReminderScheduler {
    private final CompanyRepository companyRepository;
    private final EmailService emailService;

    // runs every day at 9:00 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDeadlineReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Company> companiesWithTomorrowDeadline =
                companyRepository.findAllByDeadline(tomorrow);

        if (companiesWithTomorrowDeadline.isEmpty()) {
            System.out.println("[Scheduler] No deadlines tomorrow. Skipping.");
            return;
        }

        System.out.println("[Scheduler] Found " + companiesWithTomorrowDeadline.size()
                + " deadline(s) for tomorrow: " + tomorrow);

        for (Company company : companiesWithTomorrowDeadline) {
            try {
                emailService.sendDeadlineReminder(
                        company.getUser().getEmail(),
                        company.getUser().getName(),
                        company.getName(),
                        company.getRole(),
                        company.getDeadline().toString()
                );
                System.out.println("[Scheduler] Email sent to: "
                        + company.getUser().getEmail()
                        + " for company: " + company.getName());
            } catch (Exception e) {
                System.err.println("[Scheduler] Failed to send email to "
                        + company.getUser().getEmail()
                        + " — " + e.getMessage());
            }
        }
    }

    // test method — triggers immediately on app startup (REMOVE before production)
    // uncomment this to test email without waiting for 9 AM
    // @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    // public void testSendDeadlineReminders() {
    //     sendDeadlineReminders();
    // }
}

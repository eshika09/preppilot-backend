package com.example.preppilot.demo.controller;

import com.example.preppilot.demo.service.DeadlineReminderScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
public class SchedulerController {
    private final DeadlineReminderScheduler scheduler;

    // manually trigger for testing/demo — remove in production
    @PostMapping("/trigger-reminders")
    public ResponseEntity<String> triggerReminders() {
        scheduler.sendDeadlineReminders();
        return ResponseEntity.ok("Deadline reminders triggered successfully");
    }
}

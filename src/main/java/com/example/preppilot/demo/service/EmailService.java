package com.example.preppilot.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendDeadlineReminder(String toEmail,
                                     String userName,
                                     String companyName,
                                     String role,
                                     String deadline) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("⏰ PrepPilot Reminder: " + companyName + " deadline is tomorrow!");
        message.setText(
                "Hi " + userName + ",\n\n" +
                        "This is a reminder that your application deadline for:\n\n" +
                        "🏢 Company: " + companyName + "\n" +
                        "💼 Role: " + role + "\n" +
                        "📅 Deadline: " + deadline + "\n\n" +
                        "is TOMORROW. Make sure you're prepared!\n\n" +
                        "Head to PrepPilot to review your prep plan and resources.\n\n" +
                        "Best of luck! 🚀\n" +
                        "— PrepPilot Team"
        );

        mailSender.send(message);
    }
}

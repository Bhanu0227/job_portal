package com.jobportal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            
            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; background-color: #f8f9fa; color: #333; max-width: 600px; margin: auto; border: 1px solid #ddd;'>"
                + "<div style='text-align: center; margin-bottom: 20px;'>"
                + "<h2 style='color: #0d6efd; margin: 0;'>Job<span style='color: #333;'>Portal</span></h2>"
                + "</div>"
                + "<div style='background-color: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>"
                + "<h4 style='color: #333; margin-top: 0;'>" + subject + "</h4>"
                + "<p style='font-size: 16px; line-height: 1.5;'>" + body.replaceAll("\n", "<br>") + "</p>"
                + "</div>"
                + "<div style='margin-top: 20px; text-align: center; font-size: 12px; color: #777;'>"
                + "<p>Thank you for using JobPortal.</p>"
                + "</div>"
                + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}

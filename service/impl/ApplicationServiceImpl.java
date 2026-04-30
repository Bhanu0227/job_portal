package com.jobportal.service.impl;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.entity.Application;
import com.jobportal.entity.ApplicationStatus;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.EmailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository, JobRepository jobRepository, 
                                  UserRepository userRepository, EmailService emailService, SimpMessagingTemplate messagingTemplate) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.messagingTemplate = messagingTemplate;
    }

    private ApplicationResponse mapToDto(Application app) {
        ApplicationResponse dto = new ApplicationResponse();
        dto.setId(app.getId());
        dto.setUserId(app.getUser().getId());
        dto.setUserName(app.getUser().getName());
        dto.setUserEmail(app.getUser().getEmail());
        dto.setResumeUrl(app.getUser().getResumeUrl());
        dto.setJobId(app.getJob().getId());
        dto.setJobTitle(app.getJob().getTitle());
        dto.setStatus(app.getStatus());
        dto.setAppliedAt(app.getAppliedAt());
        return dto;
    }

    @Override
    public ApplicationResponse applyForJob(Long jobId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (applicationRepository.existsByUserIdAndJobId(user.getId(), jobId)) {
            throw new RuntimeException("You have already applied for this job");
        }

        Application application = Application.builder()
                .user(user)
                .job(job)
                .status(ApplicationStatus.APPLIED)
                .build();

        Application saved = applicationRepository.save(application);

        String messageToEmployer = "New application received from " + user.getName() + " for " + job.getTitle() + ".";
        messagingTemplate.convertAndSend("/topic/notifications/" + job.getEmployer().getId(), messageToEmployer);

        return mapToDto(saved);
    }

    @Override
    public Page<ApplicationResponse> getUserApplications(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(page, size);
        return applicationRepository.findByUserId(user.getId(), pageable).map(this::mapToDto);
    }

    @Override
    public Page<ApplicationResponse> getJobApplications(Long jobId, String employerEmail, int page, int size) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getEmployer().getEmail().equals(employerEmail)) {
            throw new RuntimeException("Unauthorized access to job applications");
        }

        Pageable pageable = PageRequest.of(page, size);
        return applicationRepository.findByJobId(jobId, pageable).map(this::mapToDto);
    }

    @Override
    public ApplicationResponse updateApplicationStatus(Long applicationId, String status, String employerEmail) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getJob().getEmployer().getEmail().equals(employerEmail)) {
            throw new RuntimeException("Unauthorized update attempt");
        }

        try {
            ApplicationStatus newStatus = ApplicationStatus.valueOf(status.toUpperCase());
            application.setStatus(newStatus);
            Application updated = applicationRepository.save(application);
            
            // Dispatch WebSocket Notification and Email upon review
            String subject = "Update: Application for " + updated.getJob().getTitle();
            String message;
            
            if (newStatus == ApplicationStatus.SHORTLISTED) {
                message = "Congratulations! You have been SHORTLISTED for the role of " + updated.getJob().getTitle() + " by " + updated.getJob().getEmployer().getName() + ".\n\nThey will be in touch with you shortly regarding the next steps.";
            } else if (newStatus == ApplicationStatus.REJECTED) {
                message = "We regret to inform you that your application for " + updated.getJob().getTitle() + " has been REJECTED. We wish you the best in your future endeavors.";
            } else {
                message = "Your application for " + updated.getJob().getTitle() + " has been " + newStatus.name();
            }

            emailService.sendEmail(updated.getUser().getEmail(), subject, message);
            messagingTemplate.convertAndSend("/topic/notifications/" + updated.getUser().getId(), message);
            
            return mapToDto(updated);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status provided");
        }
    }

    @Override
    public boolean hasUserApplied(Long jobId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return applicationRepository.existsByUserIdAndJobId(user.getId(), jobId);
    }
}

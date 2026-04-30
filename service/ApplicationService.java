package com.jobportal.service;

import com.jobportal.dto.ApplicationResponse;
import org.springframework.data.domain.Page;

public interface ApplicationService {
    ApplicationResponse applyForJob(Long jobId, String userEmail);
    Page<ApplicationResponse> getUserApplications(String userEmail, int page, int size);
    Page<ApplicationResponse> getJobApplications(Long jobId, String employerEmail, int page, int size);
    ApplicationResponse updateApplicationStatus(Long applicationId, String status, String employerEmail);
    boolean hasUserApplied(Long jobId, String userEmail);
}

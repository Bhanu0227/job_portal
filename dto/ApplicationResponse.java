package com.jobportal.dto;

import com.jobportal.entity.ApplicationStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApplicationResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String resumeUrl;
    private Long jobId;
    private String jobTitle;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}

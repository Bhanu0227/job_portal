package com.jobportal.controller;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.service.ApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/job/{jobId}")
    public ResponseEntity<ApplicationResponse> applyForJob(@PathVariable Long jobId, Authentication authentication) {
        return ResponseEntity.ok(applicationService.applyForJob(jobId, authentication.getName()));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/user")
    public ResponseEntity<Page<ApplicationResponse>> getUserApplications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(applicationService.getUserApplications(authentication.getName(), page, size));
    }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    @GetMapping("/job/{jobId}")
    public ResponseEntity<Page<ApplicationResponse>> getJobApplications(
            @PathVariable Long jobId,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(applicationService.getJobApplications(jobId, authentication.getName(), page, size));
    }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam String status,
            Authentication authentication) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(applicationId, status, authentication.getName()));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/job/{jobId}/check")
    public ResponseEntity<Boolean> checkApplicationStatus(@PathVariable Long jobId, Authentication authentication) {
        return ResponseEntity.ok(applicationService.hasUserApplied(jobId, authentication.getName()));
    }
}

package com.jobportal.controller;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.service.JobService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<Page<JobResponse>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobService.getAllJobs(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<JobResponse>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobService.searchJobs(keyword, location, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    @PostMapping
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest jobRequest, Authentication authentication) {
        return ResponseEntity.ok(jobService.createJob(jobRequest, authentication.getName()));
    }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id, @Valid @RequestBody JobRequest jobRequest, Authentication authentication) {
        return ResponseEntity.ok(jobService.updateJob(id, jobRequest, authentication.getName()));
    }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id, Authentication authentication) {
        jobService.deleteJob(id, authentication.getName());
        return ResponseEntity.ok("Job deleted successfully");
    }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    @GetMapping("/employer")
    public ResponseEntity<Page<JobResponse>> getJobsByEmployer(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobService.getJobsByEmployer(authentication.getName(), page, size));
    }
}

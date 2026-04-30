package com.jobportal.service;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import org.springframework.data.domain.Page;

public interface JobService {
    JobResponse createJob(JobRequest jobRequest, String employerEmail);
    JobResponse updateJob(Long id, JobRequest jobRequest, String employerEmail);
    void deleteJob(Long id, String employerEmail);
    JobResponse getJobById(Long id);
    Page<JobResponse> getAllJobs(int page, int size);
    Page<JobResponse> searchJobs(String keyword, String location, int page, int size);
    Page<JobResponse> getJobsByEmployer(String employerEmail, int page, int size);
}

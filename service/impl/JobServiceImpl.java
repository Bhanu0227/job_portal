package com.jobportal.service.impl;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public JobServiceImpl(JobRepository jobRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    private JobResponse mapToDto(Job job) {
        JobResponse response = new JobResponse();
        response.setId(job.getId());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setSkills(job.getSkills());
        response.setSalary(job.getSalary());
        response.setLocation(job.getLocation());
        response.setEmployerId(job.getEmployer().getId());
        response.setEmployerName(job.getEmployer().getName());
        response.setCreatedAt(job.getCreatedAt());
        return response;
    }

    @Override
    public JobResponse createJob(JobRequest jobRequest, String employerEmail) {
        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        Job job = Job.builder()
                .title(jobRequest.getTitle())
                .description(jobRequest.getDescription())
                .skills(jobRequest.getSkills())
                .salary(jobRequest.getSalary())
                .location(jobRequest.getLocation())
                .employer(employer)
                .build();

        Job savedJob = jobRepository.save(job);
        JobResponse response = mapToDto(savedJob);
        
        // Notify all subscribers about the new job
        messagingTemplate.convertAndSend("/topic/jobs", "New Job Alert: " + response.getTitle() + " at " + response.getLocation());
        
        return response;
    }

    @Override
    public JobResponse updateJob(Long id, JobRequest jobRequest, String employerEmail) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getEmployer().getEmail().equals(employerEmail)) {
            throw new RuntimeException("You do not have permission to update this job");
        }

        job.setTitle(jobRequest.getTitle());
        job.setDescription(jobRequest.getDescription());
        job.setSkills(jobRequest.getSkills());
        job.setSalary(jobRequest.getSalary());
        job.setLocation(jobRequest.getLocation());

        return mapToDto(jobRepository.save(job));
    }

    @Override
    public void deleteJob(Long id, String employerEmail) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getEmployer().getEmail().equals(employerEmail)) {
            throw new RuntimeException("You do not have permission to delete this job");
        }

        jobRepository.delete(job);
    }

    @Override
    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return mapToDto(job);
    }

    @Override
    public Page<JobResponse> getAllJobs(int page, int size) {
        return jobRepository.findAll(PageRequest.of(page, size)).map(this::mapToDto);
    }

    @Override
    public Page<JobResponse> searchJobs(String keyword, String location, int page, int size) {
        if (keyword == null) keyword = "";
        if (location == null) location = "";
        return jobRepository.findByTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(keyword, location, PageRequest.of(page, size))
                .map(this::mapToDto);
    }

    @Override
    public Page<JobResponse> getJobsByEmployer(String employerEmail, int page, int size) {
        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        return jobRepository.findByEmployerId(employer.getId(), PageRequest.of(page, size)).map(this::mapToDto);
    }
}

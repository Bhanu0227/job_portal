package com.jobportal.repository;

import com.jobportal.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    Page<Job> findByTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(String title, String location, Pageable pageable);
    Page<Job> findByEmployerId(Long employerId, Pageable pageable);
}

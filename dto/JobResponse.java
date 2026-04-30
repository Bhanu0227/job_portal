package com.jobportal.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String skills;
    private Double salary;
    private String location;
    private Long employerId;
    private String employerName;
    private LocalDateTime createdAt;
}

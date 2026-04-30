package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String skills;
    @NotNull
    private Double salary;
    @NotBlank
    private String location;
}

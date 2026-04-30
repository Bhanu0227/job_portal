package com.jobportal.service;

import org.springframework.web.multipart.MultipartFile;
import com.jobportal.dto.UserProfileResponse;

public interface UserService {
    UserProfileResponse getUserProfile(String email);
    UserProfileResponse updateProfile(String email, String name);
    String uploadResume(String email, MultipartFile file);
}

package com.jobportal.service.impl;

import com.jobportal.dto.UserProfileResponse;
import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.FileStorageService;
import com.jobportal.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public UserServiceImpl(UserRepository userRepository, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    private UserProfileResponse mapToDto(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setResumeUrl(user.getResumeUrl());
        return response;
    }

    @Override
    public UserProfileResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDto(user);
    }

    @Override
    public UserProfileResponse updateProfile(String email, String name) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(name);
        return mapToDto(userRepository.save(user));
    }

    @Override
    public String uploadResume(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        String fileName = fileStorageService.storeFile(file);
        user.setResumeUrl("/uploads/resumes/" + fileName);
        userRepository.save(user);
        return fileName;
    }
}

package com.jobportal.controller;

import com.jobportal.dto.UserProfileResponse;
import com.jobportal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserProfile(authentication.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestParam String name, Authentication authentication) {
        return ResponseEntity.ok(userService.updateProfile(authentication.getName(), name));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/resume")
    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file, Authentication authentication) {
        String fileName = userService.uploadResume(authentication.getName(), file);
        return ResponseEntity.ok("Resume uploaded successfully: " + fileName);
    }
}

package com.jobportal.service;

import java.util.Map;

public interface AdminService {
    Map<String, Object> getAnalytics();
    void deleteUser(Long userId);
    void deleteJob(Long jobId);
}

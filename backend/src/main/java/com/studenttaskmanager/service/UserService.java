package com.studenttaskmanager.service;

import com.studenttaskmanager.dto.AuthDTOs;
import com.studenttaskmanager.entity.User;
import com.studenttaskmanager.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private TaskRepository taskRepository;

    public AuthDTOs.UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toProfileResponse(user);
    }

    @Transactional
    public AuthDTOs.UserProfileResponse updateProfile(AuthDTOs.UpdateProfileRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (request.getName() != null) user.setName(request.getName());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        return toProfileResponse(userRepository.save(user));
    }

    private AuthDTOs.UserProfileResponse toProfileResponse(User user) {
        AuthDTOs.UserProfileResponse res = new AuthDTOs.UserProfileResponse();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAvatarUrl(user.getAvatarUrl());
        res.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        res.setTotalTasks((int) taskRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).size());
        res.setCompletedTasks((int) taskRepository.countByUserIdAndStatus(user.getId(),
                com.studenttaskmanager.entity.Task.TaskStatus.COMPLETED));
        return res;
    }
}

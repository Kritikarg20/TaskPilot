package com.studenttaskmanager.service;

import com.studenttaskmanager.dto.SubtaskDTOs;
import com.studenttaskmanager.entity.*;
import com.studenttaskmanager.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubtaskService {

    @Autowired private SubtaskRepository subtaskRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private UserRepository userRepository;

    public List<SubtaskDTOs.SubtaskResponse> getSubtasksByTask(Long taskId, String email) {
        return subtaskRepository.findByTaskId(taskId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public SubtaskDTOs.SubtaskResponse createSubtask(SubtaskDTOs.CreateSubtaskRequest request, String email) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        Subtask subtask = Subtask.builder()
                .title(request.getTitle())
                .completed(false)
                .task(task)
                .build();
        return toResponse(subtaskRepository.save(subtask));
    }

    @Transactional
    public SubtaskDTOs.SubtaskResponse updateSubtask(Long id, SubtaskDTOs.UpdateSubtaskRequest request, String email) {
        Subtask subtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subtask not found"));
        if (request.getTitle() != null) subtask.setTitle(request.getTitle());
        if (request.getCompleted() != null) subtask.setCompleted(request.getCompleted());
        return toResponse(subtaskRepository.save(subtask));
    }

    @Transactional
    public void deleteSubtask(Long id, String email) {
        subtaskRepository.deleteById(id);
    }

    private SubtaskDTOs.SubtaskResponse toResponse(Subtask s) {
        SubtaskDTOs.SubtaskResponse res = new SubtaskDTOs.SubtaskResponse();
        res.setId(s.getId());
        res.setTitle(s.getTitle());
        res.setCompleted(s.getCompleted());
        res.setCreatedAt(s.getCreatedAt() != null ? s.getCreatedAt().toString() : null);
        return res;
    }
}

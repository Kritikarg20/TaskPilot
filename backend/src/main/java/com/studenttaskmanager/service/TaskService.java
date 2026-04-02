package com.studenttaskmanager.service;

import com.studenttaskmanager.dto.TaskDTOs;
import com.studenttaskmanager.dto.CategoryDTOs;
import com.studenttaskmanager.dto.SubtaskDTOs;
import com.studenttaskmanager.dto.FileDTOs;
import com.studenttaskmanager.entity.*;
import com.studenttaskmanager.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;

    public List<TaskDTOs.TaskResponse> getAllTasks(String email) {
        User user = getUser(email);
        return taskRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TaskDTOs.TaskResponse getTaskById(Long id, String email) {
        User user = getUser(email);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        return toResponse(task);
    }

    @Transactional
    public TaskDTOs.TaskResponse createTask(TaskDTOs.CreateTaskRequest request, String email) {
        User user = getUser(email);
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .deadline(request.getDeadline())
                .status(request.getStatus() != null ? request.getStatus() : Task.TaskStatus.TODO)
                .kanbanColumn(Task.KanbanColumn.TODO)
                .user(user)
                .build();

        if (request.getCategoryId() != null) {
            Category cat = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            task.setCategory(cat);
        }
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskDTOs.TaskResponse updateTask(Long id, TaskDTOs.UpdateTaskRequest request, String email) {
        User user = getUser(email);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!task.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getDeadline() != null) task.setDeadline(request.getDeadline());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
            // Sync kanban column with status
            task.setKanbanColumn(Task.KanbanColumn.valueOf(request.getStatus().name()));
        }
        if (request.getKanbanColumn() != null) {
            task.setKanbanColumn(request.getKanbanColumn());
            task.setStatus(Task.TaskStatus.valueOf(request.getKanbanColumn().name()));
        }
        if (request.getCategoryId() != null) {
            Category cat = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            task.setCategory(cat);
        }
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskDTOs.TaskResponse moveKanban(Long id, TaskDTOs.KanbanMoveRequest request, String email) {
        User user = getUser(email);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!task.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");

        task.setKanbanColumn(request.getKanbanColumn());
        task.setStatus(Task.TaskStatus.valueOf(request.getKanbanColumn().name()));
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id, String email) {
        User user = getUser(email);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!task.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        taskRepository.delete(task);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public TaskDTOs.TaskResponse toResponse(Task task) {
        TaskDTOs.TaskResponse res = new TaskDTOs.TaskResponse();
        res.setId(task.getId());
        res.setTitle(task.getTitle());
        res.setDescription(task.getDescription());
        res.setPriority(task.getPriority());
        res.setDeadline(task.getDeadline());
        res.setStatus(task.getStatus());
        res.setKanbanColumn(task.getKanbanColumn());
        res.setCreatedAt(task.getCreatedAt());
        res.setUpdatedAt(task.getUpdatedAt());

        if (task.getCategory() != null) {
            CategoryDTOs.CategoryResponse catRes = new CategoryDTOs.CategoryResponse();
            catRes.setId(task.getCategory().getId());
            catRes.setName(task.getCategory().getName());
            catRes.setColor(task.getCategory().getColor());
            catRes.setIcon(task.getCategory().getIcon());
            res.setCategory(catRes);
        }

        if (task.getSubtasks() != null) {
            res.setSubtasks(task.getSubtasks().stream().map(s -> {
                SubtaskDTOs.SubtaskResponse sr = new SubtaskDTOs.SubtaskResponse();
                sr.setId(s.getId());
                sr.setTitle(s.getTitle());
                sr.setCompleted(s.getCompleted());
                sr.setCreatedAt(s.getCreatedAt() != null ? s.getCreatedAt().toString() : null);
                return sr;
            }).collect(Collectors.toList()));
        }

        if (task.getAttachments() != null) {
            res.setAttachments(task.getAttachments().stream().map(f -> {
                FileDTOs.FileResponse fr = new FileDTOs.FileResponse();
                fr.setId(f.getId());
                fr.setFileName(f.getFileName());
                fr.setFileType(f.getFileType());
                fr.setFileSize(f.getFileSize());
                fr.setDownloadUrl("/api/files/download/" + f.getId());
                fr.setCreatedAt(f.getCreatedAt() != null ? f.getCreatedAt().toString() : null);
                return fr;
            }).collect(Collectors.toList()));
        }

        return res;
    }
}

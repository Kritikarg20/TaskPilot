package com.studenttaskmanager.dto;

import com.studenttaskmanager.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class TaskDTOs {

    public static class CreateTaskRequest {
        @NotBlank(message = "Title is required")
        private String title;
        private String description;
        @NotNull(message = "Priority is required")
        private Task.Priority priority;
        private LocalDateTime deadline;
        private Task.TaskStatus status;
        private Long categoryId;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Task.Priority getPriority() { return priority; }
        public void setPriority(Task.Priority priority) { this.priority = priority; }
        public LocalDateTime getDeadline() { return deadline; }
        public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
        public Task.TaskStatus getStatus() { return status; }
        public void setStatus(Task.TaskStatus status) { this.status = status; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    }

    public static class UpdateTaskRequest {
        private String title;
        private String description;
        private Task.Priority priority;
        private LocalDateTime deadline;
        private Task.TaskStatus status;
        private Task.KanbanColumn kanbanColumn;
        private Long categoryId;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Task.Priority getPriority() { return priority; }
        public void setPriority(Task.Priority priority) { this.priority = priority; }
        public LocalDateTime getDeadline() { return deadline; }
        public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
        public Task.TaskStatus getStatus() { return status; }
        public void setStatus(Task.TaskStatus status) { this.status = status; }
        public Task.KanbanColumn getKanbanColumn() { return kanbanColumn; }
        public void setKanbanColumn(Task.KanbanColumn kanbanColumn) { this.kanbanColumn = kanbanColumn; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    }

    public static class TaskResponse {
        private Long id;
        private String title;
        private String description;
        private Task.Priority priority;
        private LocalDateTime deadline;
        private Task.TaskStatus status;
        private Task.KanbanColumn kanbanColumn;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private CategoryDTOs.CategoryResponse category;
        private List<SubtaskDTOs.SubtaskResponse> subtasks;
        private List<FileDTOs.FileResponse> attachments;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Task.Priority getPriority() { return priority; }
        public void setPriority(Task.Priority priority) { this.priority = priority; }
        public LocalDateTime getDeadline() { return deadline; }
        public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
        public Task.TaskStatus getStatus() { return status; }
        public void setStatus(Task.TaskStatus status) { this.status = status; }
        public Task.KanbanColumn getKanbanColumn() { return kanbanColumn; }
        public void setKanbanColumn(Task.KanbanColumn kanbanColumn) { this.kanbanColumn = kanbanColumn; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        public CategoryDTOs.CategoryResponse getCategory() { return category; }
        public void setCategory(CategoryDTOs.CategoryResponse category) { this.category = category; }
        public List<SubtaskDTOs.SubtaskResponse> getSubtasks() { return subtasks; }
        public void setSubtasks(List<SubtaskDTOs.SubtaskResponse> subtasks) { this.subtasks = subtasks; }
        public List<FileDTOs.FileResponse> getAttachments() { return attachments; }
        public void setAttachments(List<FileDTOs.FileResponse> attachments) { this.attachments = attachments; }
    }

    public static class KanbanMoveRequest {
        @NotNull(message = "Column is required")
        private Task.KanbanColumn kanbanColumn;

        public Task.KanbanColumn getKanbanColumn() { return kanbanColumn; }
        public void setKanbanColumn(Task.KanbanColumn kanbanColumn) { this.kanbanColumn = kanbanColumn; }
    }
}

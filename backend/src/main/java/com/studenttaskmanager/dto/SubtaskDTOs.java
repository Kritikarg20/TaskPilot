package com.studenttaskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubtaskDTOs {

    public static class CreateSubtaskRequest {
        @NotBlank(message = "Title is required")
        private String title;
        @NotNull(message = "Task ID is required")
        private Long taskId;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Long getTaskId() { return taskId; }
        public void setTaskId(Long taskId) { this.taskId = taskId; }
    }

    public static class SubtaskResponse {
        private Long id;
        private String title;
        private Boolean completed;
        private String createdAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Boolean getCompleted() { return completed; }
        public void setCompleted(Boolean completed) { this.completed = completed; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }

    public static class UpdateSubtaskRequest {
        private String title;
        private Boolean completed;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Boolean getCompleted() { return completed; }
        public void setCompleted(Boolean completed) { this.completed = completed; }
    }
}

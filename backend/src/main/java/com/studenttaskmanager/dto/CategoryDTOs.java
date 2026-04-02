package com.studenttaskmanager.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryDTOs {

    public static class CreateCategoryRequest {
        @NotBlank(message = "Name is required")
        private String name;
        private String color;
        private String icon;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
    }

    public static class CategoryResponse {
        private Long id;
        private String name;
        private String color;
        private String icon;
        private int taskCount;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public int getTaskCount() { return taskCount; }
        public void setTaskCount(int taskCount) { this.taskCount = taskCount; }
    }
}

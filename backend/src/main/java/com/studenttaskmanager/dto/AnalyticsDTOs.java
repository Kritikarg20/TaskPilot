package com.studenttaskmanager.dto;

import java.util.List;
import java.util.Map;

public class AnalyticsDTOs {

    public static class AnalyticsResponse {
        private int totalTasks;
        private int completedTasks;
        private int pendingTasks;
        private int inProgressTasks;
        private int highPriorityTasks;
        private int dueTodayTasks;
        private int overdueTasksCount;
        private List<WeeklyData> weeklyData;
        private Map<String, Integer> tasksByCategory;
        private Map<String, Integer> tasksByPriority;
        private Map<String, Integer> tasksByStatus;
        private List<UpcomingTask> upcomingDeadlines;

        public int getTotalTasks() { return totalTasks; }
        public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }
        public int getCompletedTasks() { return completedTasks; }
        public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }
        public int getPendingTasks() { return pendingTasks; }
        public void setPendingTasks(int pendingTasks) { this.pendingTasks = pendingTasks; }
        public int getInProgressTasks() { return inProgressTasks; }
        public void setInProgressTasks(int inProgressTasks) { this.inProgressTasks = inProgressTasks; }
        public int getHighPriorityTasks() { return highPriorityTasks; }
        public void setHighPriorityTasks(int highPriorityTasks) { this.highPriorityTasks = highPriorityTasks; }
        public int getDueTodayTasks() { return dueTodayTasks; }
        public void setDueTodayTasks(int dueTodayTasks) { this.dueTodayTasks = dueTodayTasks; }
        public int getOverdueTasksCount() { return overdueTasksCount; }
        public void setOverdueTasksCount(int overdueTasksCount) { this.overdueTasksCount = overdueTasksCount; }
        public List<WeeklyData> getWeeklyData() { return weeklyData; }
        public void setWeeklyData(List<WeeklyData> weeklyData) { this.weeklyData = weeklyData; }
        public Map<String, Integer> getTasksByCategory() { return tasksByCategory; }
        public void setTasksByCategory(Map<String, Integer> tasksByCategory) { this.tasksByCategory = tasksByCategory; }
        public Map<String, Integer> getTasksByPriority() { return tasksByPriority; }
        public void setTasksByPriority(Map<String, Integer> tasksByPriority) { this.tasksByPriority = tasksByPriority; }
        public Map<String, Integer> getTasksByStatus() { return tasksByStatus; }
        public void setTasksByStatus(Map<String, Integer> tasksByStatus) { this.tasksByStatus = tasksByStatus; }
        public List<UpcomingTask> getUpcomingDeadlines() { return upcomingDeadlines; }
        public void setUpcomingDeadlines(List<UpcomingTask> upcomingDeadlines) { this.upcomingDeadlines = upcomingDeadlines; }
    }

    public static class WeeklyData {
        private String day;
        private int completed;
        private int created;

        public String getDay() { return day; }
        public void setDay(String day) { this.day = day; }
        public int getCompleted() { return completed; }
        public void setCompleted(int completed) { this.completed = completed; }
        public int getCreated() { return created; }
        public void setCreated(int created) { this.created = created; }
    }

    public static class UpcomingTask {
        private Long id;
        private String title;
        private String deadline;
        private String priority;
        private String category;
        private long daysRemaining;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDeadline() { return deadline; }
        public void setDeadline(String deadline) { this.deadline = deadline; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public long getDaysRemaining() { return daysRemaining; }
        public void setDaysRemaining(long daysRemaining) { this.daysRemaining = daysRemaining; }
    }
}

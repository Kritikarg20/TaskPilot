package com.studenttaskmanager.service;

import com.studenttaskmanager.dto.AnalyticsDTOs;
import com.studenttaskmanager.entity.*;
import com.studenttaskmanager.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;

    public AnalyticsDTOs.AnalyticsResponse getAnalytics(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();

        List<Task> allTasks = taskRepository.findByUserIdOrderByCreatedAtDesc(userId);

        AnalyticsDTOs.AnalyticsResponse response = new AnalyticsDTOs.AnalyticsResponse();
        response.setTotalTasks(allTasks.size());
        response.setCompletedTasks((int) taskRepository.countByUserIdAndStatus(userId, Task.TaskStatus.COMPLETED));
        response.setPendingTasks((int) taskRepository.countByUserIdAndStatus(userId, Task.TaskStatus.TODO));
        response.setInProgressTasks((int) taskRepository.countByUserIdAndStatus(userId, Task.TaskStatus.IN_PROGRESS));
        response.setHighPriorityTasks((int) taskRepository.countByUserIdAndPriority(userId, Task.Priority.HIGH));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        response.setDueTodayTasks((int) allTasks.stream()
                .filter(t -> t.getDeadline() != null && t.getDeadline().isBefore(tomorrow) && t.getDeadline().isAfter(now))
                .count());

        response.setOverdueTasksCount((int) taskRepository.findByUserIdAndDeadlineBefore(userId, now).stream()
                .filter(t -> t.getStatus() != Task.TaskStatus.COMPLETED).count());

        // Weekly data (last 7 days)
        List<AnalyticsDTOs.WeeklyData> weeklyData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE");
        for (int i = 6; i >= 0; i--) {
            LocalDateTime dayStart = now.minusDays(i).with(LocalTime.MIDNIGHT);
            LocalDateTime dayEnd = dayStart.plusDays(1);
            int completed = taskRepository.findCompletedTasksInRange(userId, dayStart, dayEnd).size();
            int created = (int) allTasks.stream()
                    .filter(t -> t.getCreatedAt() != null && t.getCreatedAt().isAfter(dayStart) && t.getCreatedAt().isBefore(dayEnd))
                    .count();
            AnalyticsDTOs.WeeklyData wd = new AnalyticsDTOs.WeeklyData();
            wd.setDay(dayStart.format(formatter));
            wd.setCompleted(completed);
            wd.setCreated(created);
            weeklyData.add(wd);
        }
        response.setWeeklyData(weeklyData);

        // Tasks by category
        Map<String, Integer> byCategory = new LinkedHashMap<>();
        List<Category> categories = categoryRepository.findByUserIdOrderByNameAsc(userId);
        for (Category cat : categories) {
            int count = taskRepository.findByUserIdAndCategoryId(userId, cat.getId()).size();
            if (count > 0) byCategory.put(cat.getName(), count);
        }
        long uncategorized = allTasks.stream().filter(t -> t.getCategory() == null).count();
        if (uncategorized > 0) byCategory.put("Uncategorized", (int) uncategorized);
        response.setTasksByCategory(byCategory);

        // Tasks by priority
        Map<String, Integer> byPriority = new LinkedHashMap<>();
        byPriority.put("HIGH", (int) taskRepository.countByUserIdAndPriority(userId, Task.Priority.HIGH));
        byPriority.put("MEDIUM", (int) taskRepository.countByUserIdAndPriority(userId, Task.Priority.MEDIUM));
        byPriority.put("LOW", (int) taskRepository.countByUserIdAndPriority(userId, Task.Priority.LOW));
        response.setTasksByPriority(byPriority);

        // Tasks by status
        Map<String, Integer> byStatus = new LinkedHashMap<>();
        byStatus.put("TODO", (int) taskRepository.countByUserIdAndStatus(userId, Task.TaskStatus.TODO));
        byStatus.put("IN_PROGRESS", (int) taskRepository.countByUserIdAndStatus(userId, Task.TaskStatus.IN_PROGRESS));
        byStatus.put("COMPLETED", (int) taskRepository.countByUserIdAndStatus(userId, Task.TaskStatus.COMPLETED));
        response.setTasksByStatus(byStatus);

        // Upcoming deadlines (next 7 days)
        List<AnalyticsDTOs.UpcomingTask> upcoming = taskRepository.findUpcomingTasks(userId, now, now.plusDays(7))
                .stream()
                .filter(t -> t.getStatus() != Task.TaskStatus.COMPLETED)
                .limit(5)
                .map(t -> {
                    AnalyticsDTOs.UpcomingTask ut = new AnalyticsDTOs.UpcomingTask();
                    ut.setId(t.getId());
                    ut.setTitle(t.getTitle());
                    ut.setDeadline(t.getDeadline().toString());
                    ut.setPriority(t.getPriority().name());
                    ut.setCategory(t.getCategory() != null ? t.getCategory().getName() : "Uncategorized");
                    ut.setDaysRemaining(Duration.between(now, t.getDeadline()).toDays());
                    return ut;
                })
                .collect(Collectors.toList());
        response.setUpcomingDeadlines(upcoming);

        return response;
    }
}

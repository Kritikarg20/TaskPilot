package com.studenttaskmanager.repository;

import com.studenttaskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Task> findByUserIdAndStatus(Long userId, Task.TaskStatus status);

    List<Task> findByUserIdAndKanbanColumn(Long userId, Task.KanbanColumn column);

    List<Task> findByUserIdAndCategoryId(Long userId, Long categoryId);

    List<Task> findByUserIdAndDeadlineBetween(Long userId, LocalDateTime start, LocalDateTime end);

    List<Task> findByUserIdAndDeadlineBefore(Long userId, LocalDateTime date);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.deadline BETWEEN :start AND :end ORDER BY t.deadline ASC")
    List<Task> findUpcomingTasks(@Param("userId") Long userId,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.status = 'COMPLETED' AND t.updatedAt BETWEEN :start AND :end")
    List<Task> findCompletedTasksInRange(@Param("userId") Long userId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    long countByUserIdAndStatus(Long userId, Task.TaskStatus status);

    long countByUserIdAndPriority(Long userId, Task.Priority priority);
}

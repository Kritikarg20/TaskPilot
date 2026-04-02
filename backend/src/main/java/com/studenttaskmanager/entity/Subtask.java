package com.studenttaskmanager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "subtasks")
public class Subtask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Boolean completed;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (completed == null) completed = false;
    }

    public Subtask() {}

    public static SubtaskBuilder builder() { return new SubtaskBuilder(); }

    public static class SubtaskBuilder {
        private Long id; private String title; private Boolean completed; private Task task;
        public SubtaskBuilder id(Long id) { this.id = id; return this; }
        public SubtaskBuilder title(String title) { this.title = title; return this; }
        public SubtaskBuilder completed(Boolean completed) { this.completed = completed; return this; }
        public SubtaskBuilder task(Task task) { this.task = task; return this; }
        public Subtask build() {
            Subtask s = new Subtask(); s.id = id; s.title = title;
            s.completed = completed; s.task = task; return s;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }
}

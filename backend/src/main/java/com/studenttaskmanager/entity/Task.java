package com.studenttaskmanager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(name = "kanban_column")
    @Enumerated(EnumType.STRING)
    private KanbanColumn kanbanColumn;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subtask> subtasks;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FileAttachment> attachments;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = TaskStatus.TODO;
        if (kanbanColumn == null) kanbanColumn = KanbanColumn.TODO;
    }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum Priority { HIGH, MEDIUM, LOW }
    public enum TaskStatus { TODO, IN_PROGRESS, COMPLETED }
    public enum KanbanColumn { TODO, IN_PROGRESS, COMPLETED }

    public Task() {}

    // Builder
    public static TaskBuilder builder() { return new TaskBuilder(); }

    public static class TaskBuilder {
        private Long id; private String title; private String description;
        private Priority priority; private LocalDateTime deadline;
        private TaskStatus status; private KanbanColumn kanbanColumn;
        private User user; private Category category;

        public TaskBuilder id(Long id) { this.id = id; return this; }
        public TaskBuilder title(String title) { this.title = title; return this; }
        public TaskBuilder description(String description) { this.description = description; return this; }
        public TaskBuilder priority(Priority priority) { this.priority = priority; return this; }
        public TaskBuilder deadline(LocalDateTime deadline) { this.deadline = deadline; return this; }
        public TaskBuilder status(TaskStatus status) { this.status = status; return this; }
        public TaskBuilder kanbanColumn(KanbanColumn kanbanColumn) { this.kanbanColumn = kanbanColumn; return this; }
        public TaskBuilder user(User user) { this.user = user; return this; }
        public TaskBuilder category(Category category) { this.category = category; return this; }
        public Task build() {
            Task t = new Task(); t.id = id; t.title = title; t.description = description;
            t.priority = priority; t.deadline = deadline; t.status = status;
            t.kanbanColumn = kanbanColumn; t.user = user; t.category = category; return t;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public KanbanColumn getKanbanColumn() { return kanbanColumn; }
    public void setKanbanColumn(KanbanColumn kanbanColumn) { this.kanbanColumn = kanbanColumn; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public List<Subtask> getSubtasks() { return subtasks; }
    public void setSubtasks(List<Subtask> subtasks) { this.subtasks = subtasks; }
    public List<FileAttachment> getAttachments() { return attachments; }
    public void setAttachments(List<FileAttachment> attachments) { this.attachments = attachments; }
}

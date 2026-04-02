package com.studenttaskmanager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_attachments")
public class FileAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public FileAttachment() {}

    public static FileAttachmentBuilder builder() { return new FileAttachmentBuilder(); }

    public static class FileAttachmentBuilder {
        private Long id; private String fileName; private String filePath;
        private String fileType; private Long fileSize; private Task task;
        public FileAttachmentBuilder id(Long id) { this.id = id; return this; }
        public FileAttachmentBuilder fileName(String fileName) { this.fileName = fileName; return this; }
        public FileAttachmentBuilder filePath(String filePath) { this.filePath = filePath; return this; }
        public FileAttachmentBuilder fileType(String fileType) { this.fileType = fileType; return this; }
        public FileAttachmentBuilder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        public FileAttachmentBuilder task(Task task) { this.task = task; return this; }
        public FileAttachment build() {
            FileAttachment f = new FileAttachment(); f.id = id; f.fileName = fileName;
            f.filePath = filePath; f.fileType = fileType; f.fileSize = fileSize; f.task = task; return f;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }
}

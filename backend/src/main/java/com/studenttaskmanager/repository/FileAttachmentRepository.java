package com.studenttaskmanager.repository;

import com.studenttaskmanager.entity.FileAttachment;
import com.studenttaskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {
    List<FileAttachment> findByTask(Task task);
    List<FileAttachment> findByTaskId(Long taskId);
    void deleteByTask(Task task);
    void deleteByTaskId(Long taskId);
}

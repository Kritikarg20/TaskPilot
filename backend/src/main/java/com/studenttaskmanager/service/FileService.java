package com.studenttaskmanager.service;

import com.studenttaskmanager.dto.FileDTOs;
import com.studenttaskmanager.entity.FileAttachment;
import com.studenttaskmanager.entity.Task;
import com.studenttaskmanager.entity.User;
import com.studenttaskmanager.repository.FileAttachmentRepository;
import com.studenttaskmanager.repository.TaskRepository;
import com.studenttaskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Autowired
    private FileAttachmentRepository fileAttachmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public List<FileDTOs.FileResponse> getFilesByTask(Long taskId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        return fileAttachmentRepository.findByTask(task)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public FileDTOs.FileResponse uploadFile(Long taskId, MultipartFile file, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            FileAttachment attachment = new FileAttachment();
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFilePath(filePath.toString());
            attachment.setFileType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setTask(task);

            FileAttachment saved = fileAttachmentRepository.save(attachment);
            return toResponse(saved);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    public Resource downloadFile(Long fileId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        FileAttachment attachment = fileAttachmentRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        if (!attachment.getTask().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        try {
            Path filePath = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found on disk");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file: " + e.getMessage());
        }
    }

    public void deleteFile(Long fileId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        FileAttachment attachment = fileAttachmentRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        if (!attachment.getTask().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        try {
            Path filePath = Paths.get(attachment.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // log but continue
        }
        fileAttachmentRepository.delete(attachment);
    }

    private FileDTOs.FileResponse toResponse(FileAttachment attachment) {
        FileDTOs.FileResponse response = new FileDTOs.FileResponse();
        response.setId(attachment.getId());
        response.setFileName(attachment.getFileName());
        response.setFileType(attachment.getFileType());
        response.setFileSize(attachment.getFileSize());
        response.setDownloadUrl("/api/files/download/" + attachment.getId());
        return response;
    }
}
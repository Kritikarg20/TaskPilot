package com.studenttaskmanager.controller;

import com.studenttaskmanager.dto.FileDTOs;
import com.studenttaskmanager.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<FileDTOs.FileResponse>> getFilesByTask(@PathVariable Long taskId,
                                                                        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(fileService.getFilesByTask(taskId, userDetails.getUsername()));
    }

    @PostMapping("/upload/{taskId}")
    public ResponseEntity<FileDTOs.FileResponse> uploadFile(@PathVariable Long taskId,
                                                             @RequestParam("file") MultipartFile file,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(fileService.uploadFile(taskId, file, userDetails.getUsername()));
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        Resource resource = fileService.downloadFile(fileId, userDetails.getUsername());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        fileService.deleteFile(fileId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}

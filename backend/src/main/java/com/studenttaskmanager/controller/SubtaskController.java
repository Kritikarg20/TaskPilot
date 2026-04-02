package com.studenttaskmanager.controller;

import com.studenttaskmanager.dto.SubtaskDTOs;
import com.studenttaskmanager.service.SubtaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/subtasks")
public class SubtaskController {

    @Autowired
    private SubtaskService subtaskService;

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<SubtaskDTOs.SubtaskResponse>> getSubtasksByTask(@PathVariable Long taskId,
                                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subtaskService.getSubtasksByTask(taskId, userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<SubtaskDTOs.SubtaskResponse> createSubtask(@Valid @RequestBody SubtaskDTOs.CreateSubtaskRequest request,
                                                                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subtaskService.createSubtask(request, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubtaskDTOs.SubtaskResponse> updateSubtask(@PathVariable Long id,
                                                                       @RequestBody SubtaskDTOs.UpdateSubtaskRequest request,
                                                                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subtaskService.updateSubtask(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubtask(@PathVariable Long id,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        subtaskService.deleteSubtask(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}

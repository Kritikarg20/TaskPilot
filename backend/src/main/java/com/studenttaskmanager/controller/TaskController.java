package com.studenttaskmanager.controller;

import com.studenttaskmanager.dto.TaskDTOs;
import com.studenttaskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskDTOs.TaskResponse>> getAllTasks(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.getAllTasks(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTOs.TaskResponse> getTaskById(@PathVariable Long id,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.getTaskById(id, userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<TaskDTOs.TaskResponse> createTask(@Valid @RequestBody TaskDTOs.CreateTaskRequest request,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.createTask(request, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTOs.TaskResponse> updateTask(@PathVariable Long id,
                                                             @RequestBody TaskDTOs.UpdateTaskRequest request,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.updateTask(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        taskService.deleteTask(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDTOs.TaskResponse> updateTaskStatus(@PathVariable Long id,
                                                                    @RequestBody TaskDTOs.UpdateTaskRequest request,
                                                                    @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.updateTask(id, request, userDetails.getUsername()));
    }

    @PatchMapping("/{id}/kanban")
    public ResponseEntity<TaskDTOs.TaskResponse> moveKanban(@PathVariable Long id,
                                                              @RequestBody TaskDTOs.KanbanMoveRequest request,
                                                              @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.moveKanban(id, request, userDetails.getUsername()));
    }
}

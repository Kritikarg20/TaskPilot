package com.studenttaskmanager.controller;

import com.studenttaskmanager.dto.CategoryDTOs;
import com.studenttaskmanager.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDTOs.CategoryResponse>> getAllCategories(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(categoryService.getAllCategories(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<CategoryDTOs.CategoryResponse> createCategory(@Valid @RequestBody CategoryDTOs.CreateCategoryRequest request,
                                                                          @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(categoryService.createCategory(request, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTOs.CategoryResponse> updateCategory(@PathVariable Long id,
                                                                          @RequestBody CategoryDTOs.CreateCategoryRequest request,
                                                                          @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        categoryService.deleteCategory(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}

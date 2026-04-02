package com.studenttaskmanager.service;

import com.studenttaskmanager.dto.CategoryDTOs;
import com.studenttaskmanager.entity.*;
import com.studenttaskmanager.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TaskRepository taskRepository;

    public List<CategoryDTOs.CategoryResponse> getAllCategories(String email) {
        User user = getUser(email);
        return categoryRepository.findByUserIdOrderByNameAsc(user.getId())
                .stream().map(c -> toResponse(c, user.getId())).collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTOs.CategoryResponse createCategory(CategoryDTOs.CreateCategoryRequest request, String email) {
        User user = getUser(email);
        Category category = Category.builder()
                .name(request.getName())
                .color(request.getColor() != null ? request.getColor() : "#6366f1")
                .icon(request.getIcon() != null ? request.getIcon() : "📚")
                .user(user)
                .build();
        return toResponse(categoryRepository.save(category), user.getId());
    }

    @Transactional
    public CategoryDTOs.CategoryResponse updateCategory(Long id, CategoryDTOs.CreateCategoryRequest request, String email) {
        User user = getUser(email);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (!category.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        if (request.getName() != null) category.setName(request.getName());
        if (request.getColor() != null) category.setColor(request.getColor());
        if (request.getIcon() != null) category.setIcon(request.getIcon());
        return toResponse(categoryRepository.save(category), user.getId());
    }

    @Transactional
    public void deleteCategory(Long id, String email) {
        User user = getUser(email);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (!category.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        categoryRepository.delete(category);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private CategoryDTOs.CategoryResponse toResponse(Category c, Long userId) {
        CategoryDTOs.CategoryResponse res = new CategoryDTOs.CategoryResponse();
        res.setId(c.getId());
        res.setName(c.getName());
        res.setColor(c.getColor());
        res.setIcon(c.getIcon());
        res.setTaskCount((int) taskRepository.findByUserIdAndCategoryId(userId, c.getId()).stream().count());
        return res;
    }
}

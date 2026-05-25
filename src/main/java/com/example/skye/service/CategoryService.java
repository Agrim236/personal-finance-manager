package com.example.skye.service;

import com.example.skye.dto.*;
import com.example.skye.entity.CategoryEntity;
import com.example.skye.entity.UserEntity;
import com.example.skye.repository.CategoryRepository;
import com.example.skye.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Manages default and custom categories for the authenticated user.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    public CategoryService(CategoryRepository categoryRepository,
                           TransactionRepository transactionRepository,
                           CurrentUserService currentUserService) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.currentUserService = currentUserService;
    }

    public CategoriesListResponse getAllCategories() {
        UserEntity user = currentUserService.getCurrentUser();
        List<CategoryResponse> categories = categoryRepository.findAllAccessible(user.getId()).stream()
                .map(this::toResponse)
                .toList();
        return new CategoriesListResponse(categories);
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        UserEntity user = currentUserService.getCurrentUser();
        String name = request.getName().trim();
        String type = request.getType().toUpperCase();

        if (categoryRepository.findAccessibleByName(name, user.getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists");
        }

        CategoryEntity category = new CategoryEntity();
        category.setName(name);
        category.setType(type);
        category.setCustom(true);
        category.setUser(user);
        return toResponse(categoryRepository.save(category));
    }

    public MessageResponse deleteCategory(String name) {
        UserEntity user = currentUserService.getCurrentUser();
        String trimmedName = name.trim();

        categoryRepository.findAccessibleByName(trimmedName, user.getId())
                .filter(category -> category.getUser() == null)
                .ifPresent(category -> {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Default categories cannot be deleted");
                });

        CategoryEntity category = categoryRepository.findByNameAndUser_Id(trimmedName, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        if (transactionRepository.countByCategory_Id(category.getId()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category is referenced by transactions");
        }

        categoryRepository.delete(category);
        return new MessageResponse("Category deleted successfully");
    }

    public CategoryEntity resolveCategory(String categoryName, Long userId) {
        return categoryRepository.findAccessibleByName(categoryName.trim(), userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category"));
    }

    private CategoryResponse toResponse(CategoryEntity category) {
        return CategoryResponse.builder()
                .name(category.getName())
                .type(category.getType())
                .custom(category.isCustom())
                .build();
    }
}

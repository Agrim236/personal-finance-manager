package com.example.skye.controller;

import com.example.skye.dto.CategoriesListResponse;
import com.example.skye.dto.CategoryRequest;
import com.example.skye.dto.CategoryResponse;
import com.example.skye.dto.MessageResponse;
import com.example.skye.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private CategoryController categoryController;

    @Test
    void getAll_returnsOk() {
        when(categoryService.getAllCategories()).thenReturn(new CategoriesListResponse(List.of()));
        assertEquals(HttpStatus.OK, categoryController.getAll().getStatusCode());
    }

    @Test
    void create_returnsCreated() {
        CategoryResponse body = CategoryResponse.builder().name("X").type("INCOME").custom(true).build();
        when(categoryService.createCategory(org.mockito.ArgumentMatchers.any(CategoryRequest.class))).thenReturn(body);
        assertEquals(HttpStatus.CREATED, categoryController.create(new CategoryRequest()).getStatusCode());
    }

    @Test
    void delete_returnsOk() {
        when(categoryService.deleteCategory("X")).thenReturn(new MessageResponse("Category deleted successfully"));
        assertEquals(HttpStatus.OK, categoryController.delete("X").getStatusCode());
    }
}

package com.example.skye.service;

import com.example.skye.dto.CategoryRequest;
import com.example.skye.dto.CategoryResponse;
import com.example.skye.dto.CategoriesListResponse;
import com.example.skye.entity.CategoryEntity;
import com.example.skye.entity.UserEntity;
import com.example.skye.repository.CategoryRepository;
import com.example.skye.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CurrentUserService currentUserService;
    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_duplicateName_throwsConflict() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findAccessibleByName("Freelance", 1L)).thenReturn(Optional.of(new CategoryEntity()));

        CategoryRequest request = new CategoryRequest();
        request.setName("Freelance");
        request.setType("INCOME");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> categoryService.createCategory(request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void createCategory_success() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findAccessibleByName("SideBiz", 1L)).thenReturn(Optional.empty());

        CategoryEntity saved = new CategoryEntity();
        saved.setName("SideBiz");
        saved.setType("INCOME");
        saved.setCustom(true);
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(saved);

        CategoryRequest request = new CategoryRequest();
        request.setName("SideBiz");
        request.setType("INCOME");

        CategoryResponse response = categoryService.createCategory(request);
        assertEquals("SideBiz", response.getName());
        assertTrue(response.isCustom());
    }

    @Test
    void deleteDefaultCategory_throwsForbidden() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        CategoryEntity defaultCategory = new CategoryEntity();
        defaultCategory.setName("Food");
        defaultCategory.setCustom(false);
        defaultCategory.setUser(null);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findAccessibleByName("Food", 1L)).thenReturn(Optional.of(defaultCategory));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> categoryService.deleteCategory("Food"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void deleteCategory_inUse_throwsBadRequest() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        CategoryEntity custom = new CategoryEntity();
        custom.setId(9L);
        custom.setName("Gym");
        custom.setCustom(true);
        custom.setUser(user);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findAccessibleByName("Gym", 1L)).thenReturn(Optional.of(custom));
        when(categoryRepository.findByNameAndUser_Id("Gym", 1L)).thenReturn(Optional.of(custom));
        when(transactionRepository.countByCategory_Id(9L)).thenReturn(2L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> categoryService.deleteCategory("Gym"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void deleteCategory_unusedCustom_success() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        CategoryEntity custom = new CategoryEntity();
        custom.setId(9L);
        custom.setName("Gym");
        custom.setCustom(true);
        custom.setUser(user);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findAccessibleByName("Gym", 1L)).thenReturn(Optional.of(custom));
        when(categoryRepository.findByNameAndUser_Id("Gym", 1L)).thenReturn(Optional.of(custom));
        when(transactionRepository.countByCategory_Id(9L)).thenReturn(0L);

        assertEquals("Category deleted successfully", categoryService.deleteCategory("Gym").getMessage());
        verify(categoryRepository).delete(custom);
    }

    @Test
    void getAllCategories_returnsAccessibleList() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        CategoryEntity salary = new CategoryEntity();
        salary.setName("Salary");
        salary.setType("INCOME");
        salary.setCustom(false);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findAllAccessible(1L)).thenReturn(List.of(salary));

        CategoriesListResponse response = categoryService.getAllCategories();
        assertEquals(1, response.getCategories().size());
        assertEquals("Salary", response.getCategories().get(0).getName());
    }

    @Test
    void resolveCategory_invalid_throwsBadRequest() {
        when(categoryRepository.findAccessibleByName("Missing", 1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> categoryService.resolveCategory("Missing", 1L));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}

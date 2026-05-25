package com.example.skye.service;

import com.example.skye.dto.TransactionRequest;
import com.example.skye.dto.TransactionResponse;
import com.example.skye.dto.TransactionUpdateRequest;
import com.example.skye.dto.TransactionsListResponse;
import com.example.skye.entity.CategoryEntity;
import com.example.skye.entity.TransactionEntity;
import com.example.skye.entity.UserEntity;
import com.example.skye.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private CurrentUserService currentUserService;
    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createTransaction_futureDate_throwsBadRequest() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(currentUserService.getCurrentUser()).thenReturn(user);

        TransactionRequest request = new TransactionRequest();
        request.setAmount(BigDecimal.TEN);
        request.setCategory("Salary");
        request.setDate(LocalDate.now().plusDays(1));

        assertThrows(ResponseStatusException.class, () -> transactionService.create(request));
    }

    @Test
    void createTransaction_success() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        CategoryEntity category = new CategoryEntity();
        category.setName("Salary");
        category.setType("INCOME");

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(categoryService.resolveCategory("Salary", 1L)).thenReturn(category);

        TransactionEntity saved = new TransactionEntity();
        saved.setId(5L);
        saved.setAmount(new BigDecimal("5000.00"));
        saved.setDate(LocalDate.of(2024, 1, 15));
        saved.setCategory(category);
        saved.setDescription("Pay");
        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(saved);

        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("5000.00"));
        request.setCategory("Salary");
        request.setDate(LocalDate.of(2024, 1, 15));
        request.setDescription("Pay");

        TransactionResponse response = transactionService.create(request);
        assertEquals(5L, response.getId());
        assertEquals("INCOME", response.getType());
        assertEquals(new BigDecimal("5000.00"), response.getAmount());
    }

    @Test
    void getAll_filtersByCategory() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        CategoryEntity category = new CategoryEntity();
        category.setName("Salary");
        category.setType("INCOME");
        TransactionEntity tx = new TransactionEntity();
        tx.setId(1L);
        tx.setAmount(new BigDecimal("100.00"));
        tx.setDate(LocalDate.of(2024, 1, 1));
        tx.setCategory(category);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findFiltered(eq(1L), eq(null), eq(null), eq(null), eq("Salary"), eq(null)))
                .thenReturn(List.of(tx));

        TransactionsListResponse response = transactionService.getAll(null, null, null, "Salary", null);
        assertEquals(1, response.getTransactions().size());
    }

    @Test
    void updateTransaction_changesAmount_keepsDate() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        CategoryEntity category = new CategoryEntity();
        category.setName("Salary");
        category.setType("INCOME");
        TransactionEntity tx = new TransactionEntity();
        tx.setId(3L);
        tx.setUser(user);
        tx.setCategory(category);
        tx.setAmount(new BigDecimal("5000.00"));
        tx.setDate(LocalDate.of(2024, 1, 15));

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findById(3L)).thenReturn(Optional.of(tx));
        when(transactionRepository.save(tx)).thenReturn(tx);

        TransactionUpdateRequest update = new TransactionUpdateRequest();
        update.setAmount(new BigDecimal("5500.00"));
        update.setDate(LocalDate.of(2024, 2, 1));

        TransactionResponse response = transactionService.update(3L, update);
        assertEquals(LocalDate.of(2024, 1, 15), response.getDate());
        assertEquals(new BigDecimal("5500.00"), response.getAmount());
    }

    @Test
    void updateTransaction_otherUser_throwsForbidden() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        UserEntity owner = new UserEntity();
        owner.setId(2L);
        TransactionEntity tx = new TransactionEntity();
        tx.setId(3L);
        tx.setUser(owner);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findById(3L)).thenReturn(Optional.of(tx));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> transactionService.update(3L, new TransactionUpdateRequest()));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void deleteTransaction_notFound_throwsNotFound() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> transactionService.delete(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void deleteTransaction_success() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        TransactionEntity tx = new TransactionEntity();
        tx.setId(3L);
        tx.setUser(user);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findById(3L)).thenReturn(Optional.of(tx));

        assertEquals("Transaction deleted successfully", transactionService.delete(3L).getMessage());
        verify(transactionRepository).delete(tx);
    }
}

package com.example.skye.service;

import com.example.skye.dto.*;
import com.example.skye.entity.CategoryEntity;
import com.example.skye.entity.TransactionEntity;
import com.example.skye.entity.UserEntity;
import com.example.skye.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * Business logic for creating, listing, updating, and deleting financial transactions.
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;
    private final CurrentUserService currentUserService;

    public TransactionService(TransactionRepository transactionRepository,
                              CategoryService categoryService,
                              CurrentUserService currentUserService) {
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
        this.currentUserService = currentUserService;
    }

    public TransactionResponse create(TransactionRequest request) {
        UserEntity user = currentUserService.getCurrentUser();
        validateDate(request.getDate());

        CategoryEntity category = categoryService.resolveCategory(request.getCategory(), user.getId());

        TransactionEntity transaction = new TransactionEntity();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(request.getAmount().setScale(2, java.math.RoundingMode.HALF_UP));
        transaction.setDate(request.getDate());
        transaction.setDescription(request.getDescription());

        return toResponse(transactionRepository.save(transaction));
    }

    public TransactionsListResponse getAll(LocalDate startDate, LocalDate endDate,
                                           Long categoryId, String category, String type) {
        UserEntity user = currentUserService.getCurrentUser();
        String normalizedType = type != null ? type.toUpperCase() : null;
        if (normalizedType != null && !normalizedType.equals("INCOME") && !normalizedType.equals("EXPENSE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Type must be INCOME or EXPENSE");
        }

        String categoryName = (category != null && !category.isBlank()) ? category.trim() : null;

        List<TransactionResponse> transactions = transactionRepository
                .findFiltered(user.getId(), startDate, endDate, categoryId, categoryName, normalizedType)
                .stream()
                .map(this::toResponse)
                .toList();

        return new TransactionsListResponse(transactions);
    }

    public TransactionResponse update(Long id, TransactionUpdateRequest request) {
        UserEntity user = currentUserService.getCurrentUser();
        TransactionEntity transaction = getOwnedTransaction(id, user.getId());

        if (request.getAmount() != null) {
            if (request.getAmount().signum() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be a positive value");
            }
            transaction.setAmount(request.getAmount().setScale(2, java.math.RoundingMode.HALF_UP));
        }
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            CategoryEntity category = categoryService.resolveCategory(request.getCategory(), user.getId());
            transaction.setCategory(category);
        }
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }

        return toResponse(transactionRepository.save(transaction));
    }

    public MessageResponse delete(Long id) {
        UserEntity user = currentUserService.getCurrentUser();
        TransactionEntity transaction = getOwnedTransaction(id, user.getId());
        transactionRepository.delete(transaction);
        return new MessageResponse("Transaction deleted successfully");
    }

    private TransactionEntity getOwnedTransaction(Long id, Long userId) {
        TransactionEntity transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return transaction;
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date is required");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction date cannot be in the future");
        }
    }

    private TransactionResponse toResponse(TransactionEntity transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .category(transaction.getCategory().getName())
                .description(transaction.getDescription())
                .type(transaction.getCategory().getType())
                .build();
    }
}

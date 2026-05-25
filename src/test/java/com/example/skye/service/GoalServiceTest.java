package com.example.skye.service;

import com.example.skye.dto.GoalRequest;
import com.example.skye.dto.GoalResponse;
import com.example.skye.dto.GoalUpdateRequest;
import com.example.skye.entity.SavingsGoalEntity;
import com.example.skye.entity.UserEntity;
import com.example.skye.repository.SavingsGoalRepository;
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
class GoalServiceTest {

    @Mock
    private SavingsGoalRepository goalRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CurrentUserService currentUserService;
    @InjectMocks
    private GoalService goalService;

    @Test
    void createGoal_startDateAfterTargetDate_throwsBadRequest() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(currentUserService.getCurrentUser()).thenReturn(user);

        GoalRequest request = new GoalRequest();
        request.setGoalName("Invalid");
        request.setTargetAmount(new BigDecimal("5000.00"));
        request.setTargetDate(LocalDate.now().plusYears(1));
        request.setStartDate(LocalDate.now().plusYears(2));

        assertThrows(ResponseStatusException.class, () -> goalService.create(request));
    }

    @Test
    void createGoal_pastTargetDate_throwsBadRequest() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(currentUserService.getCurrentUser()).thenReturn(user);

        GoalRequest request = new GoalRequest();
        request.setGoalName("Invalid");
        request.setTargetAmount(new BigDecimal("5000.00"));
        request.setTargetDate(LocalDate.now().minusDays(1));

        assertThrows(ResponseStatusException.class, () -> goalService.create(request));
    }

    @Test
    void createGoal_computesProgress() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(currentUserService.getCurrentUser()).thenReturn(user);

        SavingsGoalEntity saved = new SavingsGoalEntity();
        saved.setId(1L);
        saved.setUser(user);
        saved.setGoalName("Emergency");
        saved.setTargetAmount(new BigDecimal("10000.00"));
        saved.setTargetDate(LocalDate.now().plusYears(2));
        saved.setStartDate(LocalDate.of(2024, 1, 1));
        when(goalRepository.save(any(SavingsGoalEntity.class))).thenReturn(saved);
        when(transactionRepository.sumByUserTypeSince(eq(1L), eq("INCOME"), eq(LocalDate.of(2024, 1, 1))))
                .thenReturn(new BigDecimal("7000.00"));
        when(transactionRepository.sumByUserTypeSince(eq(1L), eq("EXPENSE"), eq(LocalDate.of(2024, 1, 1))))
                .thenReturn(new BigDecimal("450.00"));

        GoalRequest request = new GoalRequest();
        request.setGoalName("Emergency");
        request.setTargetAmount(new BigDecimal("10000.00"));
        request.setTargetDate(LocalDate.now().plusYears(2));
        request.setStartDate(LocalDate.of(2024, 1, 1));

        GoalResponse response = goalService.create(request);
        assertEquals(new BigDecimal("6550.00"), response.getCurrentProgress());
        assertEquals(65.5, response.getProgressPercentage());
        assertEquals(new BigDecimal("3450.00"), response.getRemainingAmount());
    }

    @Test
    void updateGoal_targetAmount_recalculatesProgress() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        SavingsGoalEntity goal = new SavingsGoalEntity();
        goal.setId(2L);
        goal.setUser(user);
        goal.setGoalName("Fund");
        goal.setTargetAmount(new BigDecimal("10000.00"));
        goal.setTargetDate(LocalDate.now().plusYears(2));
        goal.setStartDate(LocalDate.of(2024, 1, 1));

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(goalRepository.findById(2L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(goal)).thenReturn(goal);
        when(transactionRepository.sumByUserTypeSince(eq(1L), eq("INCOME"), any(LocalDate.class)))
                .thenReturn(new BigDecimal("10000.00"));
        when(transactionRepository.sumByUserTypeSince(eq(1L), eq("EXPENSE"), any(LocalDate.class)))
                .thenReturn(new BigDecimal("950.00"));

        GoalUpdateRequest update = new GoalUpdateRequest();
        update.setTargetAmount(new BigDecimal("15000.00"));

        GoalResponse response = goalService.update(2L, update);
        assertEquals(60.33, response.getProgressPercentage());
        assertEquals(new BigDecimal("5950.00"), response.getRemainingAmount());
    }

    @Test
    void getAllGoals_returnsUserGoals() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        SavingsGoalEntity goal = new SavingsGoalEntity();
        goal.setId(1L);
        goal.setUser(user);
        goal.setGoalName("A");
        goal.setTargetAmount(new BigDecimal("1000.00"));
        goal.setTargetDate(LocalDate.now().plusYears(1));
        goal.setStartDate(LocalDate.now());

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(goalRepository.findByUser_Id(1L)).thenReturn(List.of(goal));
        when(transactionRepository.sumByUserTypeSince(eq(1L), eq("INCOME"), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumByUserTypeSince(eq(1L), eq("EXPENSE"), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);

        assertEquals(1, goalService.getAll().getGoals().size());
    }

    @Test
    void deleteGoal_success() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        SavingsGoalEntity goal = new SavingsGoalEntity();
        goal.setId(4L);
        goal.setUser(user);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(goalRepository.findById(4L)).thenReturn(Optional.of(goal));

        assertEquals("Goal deleted successfully", goalService.delete(4L).getMessage());
        verify(goalRepository).delete(goal);
    }
}

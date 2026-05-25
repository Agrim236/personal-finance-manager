package com.example.skye.service;

import com.example.skye.dto.*;
import com.example.skye.entity.SavingsGoalEntity;
import com.example.skye.entity.UserEntity;
import com.example.skye.repository.SavingsGoalRepository;
import com.example.skye.repository.TransactionRepository;
import com.example.skye.util.GoalProgressCalculator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Manages savings goals and computes progress from income minus expenses since each goal's start date.
 */
@Service
public class GoalService {

    private final SavingsGoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    public GoalService(SavingsGoalRepository goalRepository,
                       TransactionRepository transactionRepository,
                       CurrentUserService currentUserService) {
        this.goalRepository = goalRepository;
        this.transactionRepository = transactionRepository;
        this.currentUserService = currentUserService;
    }

    public GoalResponse create(GoalRequest request) {
        UserEntity user = currentUserService.getCurrentUser();

        if (request.getTargetDate() == null || !request.getTargetDate().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target date must be a future date");
        }

        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();
        if (startDate.isAfter(request.getTargetDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be on or before target date");
        }

        SavingsGoalEntity goal = new SavingsGoalEntity();
        goal.setUser(user);
        goal.setGoalName(request.getGoalName().trim());
        goal.setTargetAmount(request.getTargetAmount().setScale(2, RoundingMode.HALF_UP));
        goal.setTargetDate(request.getTargetDate());
        goal.setStartDate(startDate);

        return toResponse(goalRepository.save(goal));
    }

    public GoalsListResponse getAll() {
        UserEntity user = currentUserService.getCurrentUser();
        List<GoalResponse> goals = goalRepository.findByUser_Id(user.getId()).stream()
                .map(this::toResponse)
                .toList();
        return new GoalsListResponse(goals);
    }

    public GoalResponse getById(Long id) {
        UserEntity user = currentUserService.getCurrentUser();
        SavingsGoalEntity goal = getOwnedGoal(id, user.getId());
        return toResponse(goal);
    }

    public GoalResponse update(Long id, GoalUpdateRequest request) {
        UserEntity user = currentUserService.getCurrentUser();
        SavingsGoalEntity goal = getOwnedGoal(id, user.getId());

        if (request.getTargetAmount() != null) {
            if (request.getTargetAmount().signum() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target amount must be positive");
            }
            goal.setTargetAmount(request.getTargetAmount().setScale(2, RoundingMode.HALF_UP));
        }
        if (request.getTargetDate() != null) {
            if (!request.getTargetDate().isAfter(LocalDate.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target date must be a future date");
            }
            if (goal.getStartDate().isAfter(request.getTargetDate())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be on or before target date");
            }
            goal.setTargetDate(request.getTargetDate());
        }

        return toResponse(goalRepository.save(goal));
    }

    public MessageResponse delete(Long id) {
        UserEntity user = currentUserService.getCurrentUser();
        SavingsGoalEntity goal = getOwnedGoal(id, user.getId());
        goalRepository.delete(goal);
        return new MessageResponse("Goal deleted successfully");
    }

    private SavingsGoalEntity getOwnedGoal(Long id, Long userId) {
        SavingsGoalEntity goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found"));
        if (!goal.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return goal;
    }

    private GoalResponse toResponse(SavingsGoalEntity goal) {
        BigDecimal income = transactionRepository.sumByUserTypeSince(
                goal.getUser().getId(), "INCOME", goal.getStartDate());
        BigDecimal expenses = transactionRepository.sumByUserTypeSince(
                goal.getUser().getId(), "EXPENSE", goal.getStartDate());
        BigDecimal currentProgress = income.subtract(expenses).setScale(2, RoundingMode.HALF_UP);
        double progressPercentage = GoalProgressCalculator.calculateProgressPercentage(
                currentProgress, goal.getTargetAmount());
        BigDecimal remainingAmount = goal.getTargetAmount().subtract(currentProgress).setScale(2, RoundingMode.HALF_UP);

        return GoalResponse.builder()
                .id(goal.getId())
                .goalName(goal.getGoalName())
                .targetAmount(goal.getTargetAmount())
                .targetDate(goal.getTargetDate())
                .startDate(goal.getStartDate())
                .currentProgress(currentProgress)
                .progressPercentage(progressPercentage)
                .remainingAmount(remainingAmount)
                .build();
    }
}

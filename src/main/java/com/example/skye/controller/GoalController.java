package com.example.skye.controller;

import com.example.skye.dto.*;
import com.example.skye.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<GoalResponse> create(@Valid @RequestBody GoalRequest request) {
        return new ResponseEntity<>(goalService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<GoalsListResponse> getAll() {
        return ResponseEntity.ok(goalService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(goalService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> update(@PathVariable Long id, @Valid @RequestBody GoalUpdateRequest request) {
        return ResponseEntity.ok(goalService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(goalService.delete(id));
    }
}

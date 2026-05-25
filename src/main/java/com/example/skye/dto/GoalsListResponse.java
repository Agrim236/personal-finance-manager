package com.example.skye.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GoalsListResponse {
    private List<GoalResponse> goals = new ArrayList<>();

    public GoalsListResponse(List<GoalResponse> goals) {
        this.goals = goals != null ? goals : new ArrayList<>();
    }
}

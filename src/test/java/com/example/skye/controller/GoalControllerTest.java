package com.example.skye.controller;

import com.example.skye.dto.GoalRequest;
import com.example.skye.dto.GoalResponse;
import com.example.skye.dto.GoalUpdateRequest;
import com.example.skye.dto.GoalsListResponse;
import com.example.skye.dto.MessageResponse;
import com.example.skye.service.GoalService;
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
class GoalControllerTest {

    @Mock
    private GoalService goalService;
    @InjectMocks
    private GoalController goalController;

    @Test
    void create_returnsCreated() {
        when(goalService.create(org.mockito.ArgumentMatchers.any(GoalRequest.class)))
                .thenReturn(GoalResponse.builder().id(1L).build());
        assertEquals(HttpStatus.CREATED, goalController.create(new GoalRequest()).getStatusCode());
    }

    @Test
    void getAll_returnsOk() {
        when(goalService.getAll()).thenReturn(new GoalsListResponse(List.of()));
        assertEquals(HttpStatus.OK, goalController.getAll().getStatusCode());
    }

    @Test
    void getById_returnsOk() {
        when(goalService.getById(1L)).thenReturn(GoalResponse.builder().id(1L).build());
        assertEquals(HttpStatus.OK, goalController.getById(1L).getStatusCode());
    }

    @Test
    void update_returnsOk() {
        when(goalService.update(1L, new GoalUpdateRequest())).thenReturn(GoalResponse.builder().id(1L).build());
        assertEquals(HttpStatus.OK, goalController.update(1L, new GoalUpdateRequest()).getStatusCode());
    }

    @Test
    void delete_returnsOk() {
        when(goalService.delete(1L)).thenReturn(new MessageResponse("Goal deleted successfully"));
        assertEquals(HttpStatus.OK, goalController.delete(1L).getStatusCode());
    }
}

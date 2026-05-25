package com.example.skye.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Category returned by the categories API (field {@code custom} matches the official E2E test script).
 */
@Data
@Builder
public class CategoryResponse {
    private String name;
    private String type;
    private boolean custom;
}

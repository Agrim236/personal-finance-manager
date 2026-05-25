package com.example.skye.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "(?i)INCOME|EXPENSE", message = "Type must be INCOME or EXPENSE")
    private String type;
}

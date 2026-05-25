package com.example.skye.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoriesListResponse {
    private List<CategoryResponse> categories;
}

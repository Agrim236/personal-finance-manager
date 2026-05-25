package com.example.skye.config;

import com.example.skye.entity.CategoryEntity;
import com.example.skye.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public DataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.countByUserIsNull() > 0) {
            return;
        }

        createDefault("Salary", "INCOME");
        createDefault("Food", "EXPENSE");
        createDefault("Rent", "EXPENSE");
        createDefault("Transportation", "EXPENSE");
        createDefault("Entertainment", "EXPENSE");
        createDefault("Healthcare", "EXPENSE");
        createDefault("Utilities", "EXPENSE");
    }

    private void createDefault(String name, String type) {
        CategoryEntity category = new CategoryEntity();
        category.setName(name);
        category.setType(type);
        category.setCustom(false);
        category.setUser(null);
        categoryRepository.save(category);
    }
}

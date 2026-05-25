package com.example.skye.repository;

import com.example.skye.entity.SavingsGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoalEntity, Long> {

    List<SavingsGoalEntity> findByUser_Id(Long userId);

    Optional<SavingsGoalEntity> findByIdAndUser_Id(Long id, Long userId);
}

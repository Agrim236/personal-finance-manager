package com.example.skye.repository;

import com.example.skye.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("SELECT c FROM CategoryEntity c WHERE c.user IS NULL OR c.user.id = :userId")
    List<CategoryEntity> findAllAccessible(@Param("userId") Long userId);

    @Query("SELECT c FROM CategoryEntity c WHERE c.name = :name AND (c.user IS NULL OR c.user.id = :userId)")
    Optional<CategoryEntity> findAccessibleByName(@Param("name") String name, @Param("userId") Long userId);

    Optional<CategoryEntity> findByNameAndUser_Id(String name, Long userId);

    boolean existsByNameAndUser_Id(String name, Long userId);

    long countByUserIsNull();
}

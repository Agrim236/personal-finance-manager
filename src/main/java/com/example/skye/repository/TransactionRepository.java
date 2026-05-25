package com.example.skye.repository;

import com.example.skye.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByUser_IdOrderByDateDesc(Long userId);

    @Query("""
            SELECT t FROM TransactionEntity t
            WHERE t.user.id = :userId
            AND (:startDate IS NULL OR t.date >= :startDate)
            AND (:endDate IS NULL OR t.date <= :endDate)
            AND (:categoryId IS NULL OR t.category.id = :categoryId)
            AND (:categoryName IS NULL OR t.category.name = :categoryName)
            AND (:type IS NULL OR t.category.type = :type)
            ORDER BY t.date DESC, t.id DESC
            """)
    List<TransactionEntity> findFiltered(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("categoryId") Long categoryId,
            @Param("categoryName") String categoryName,
            @Param("type") String type);

    long countByCategory_Id(Long categoryId);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t
            WHERE t.user.id = :userId AND t.category.type = :type
            AND t.date >= :startDate
            """)
    BigDecimal sumByUserTypeSince(@Param("userId") Long userId, @Param("type") String type, @Param("startDate") LocalDate startDate);

    @Query("""
            SELECT t FROM TransactionEntity t
            WHERE t.user.id = :userId
            AND t.date >= :startDate AND t.date <= :endDate
            """)
    List<TransactionEntity> findByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}

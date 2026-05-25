package com.example.skye.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GoalProgressCalculatorTest {

    @Test
    void calculateProgressPercentage_typicalValues() {
        assertEquals(65.5, GoalProgressCalculator.calculateProgressPercentage(
                new BigDecimal("6550.00"), new BigDecimal("10000.00")));
        assertEquals(60.33, GoalProgressCalculator.calculateProgressPercentage(
                new BigDecimal("9050.00"), new BigDecimal("15000.00")));
        assertEquals(50.0, GoalProgressCalculator.calculateProgressPercentage(
                new BigDecimal("2500.00"), new BigDecimal("5000.00")));
    }

    @Test
    void calculateProgressPercentage_zeroProgress() {
        assertEquals(0.0, GoalProgressCalculator.calculateProgressPercentage(
                BigDecimal.ZERO, new BigDecimal("5000.00")));
    }
}

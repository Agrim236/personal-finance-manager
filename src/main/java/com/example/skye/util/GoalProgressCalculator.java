package com.example.skye.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Computes savings-goal progress fields used in API responses.
 */
public final class GoalProgressCalculator {

    private GoalProgressCalculator() {
    }

    public static double calculateProgressPercentage(BigDecimal currentProgress, BigDecimal targetAmount) {
        if (targetAmount == null || targetAmount.signum() <= 0) {
            return 0.0;
        }
        BigDecimal percentage = currentProgress
                .divide(targetAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return percentage.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

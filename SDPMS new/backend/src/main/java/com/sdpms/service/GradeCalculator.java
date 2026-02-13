package com.sdpms.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class GradeCalculator {

    private static final double[][] GRADE_BOUNDS = {
            {90, 10}, {80, 9}, {70, 8}, {60, 7}, {50, 6}, {45, 5}, {40, 4}, {0, 0}
    };
    private static final String[] GRADE_LETTERS = {"A+", "A", "B+", "B", "C", "D", "P", "F"};

    public static String marksToGrade(BigDecimal totalMarks, BigDecimal maxMarks) {
        if (totalMarks == null || maxMarks == null || maxMarks.compareTo(BigDecimal.ZERO) == 0)
            return "F";
        double pct = totalMarks.multiply(BigDecimal.valueOf(100)).divide(maxMarks, 2, RoundingMode.HALF_UP).doubleValue();
        for (int i = 0; i < GRADE_BOUNDS.length; i++) {
            if (pct >= GRADE_BOUNDS[i][0]) return GRADE_LETTERS[i];
        }
        return "F";
    }

    public static BigDecimal gradeToPoint(String grade) {
        return switch (grade != null ? grade.toUpperCase() : "") {
            case "A+" -> BigDecimal.valueOf(10);
            case "A" -> BigDecimal.valueOf(9);
            case "B+" -> BigDecimal.valueOf(8);
            case "B" -> BigDecimal.valueOf(7);
            case "C" -> BigDecimal.valueOf(6);
            case "D" -> BigDecimal.valueOf(5);
            case "P" -> BigDecimal.valueOf(4);
            default -> BigDecimal.ZERO;
        };
    }
}

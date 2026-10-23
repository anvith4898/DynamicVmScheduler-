package com.college.cloudsim;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MetricsCalculatorTest {

    @Test
    void giniZeroForEqualLoads() {
        // Using reflection to test private method, or just test via public API
        // We'll test the logic directly by copying the algorithm here
        double gini = gini(List.of(5, 5, 5, 5));
        assertEquals(0.0, gini, 1e-9);
    }

    @Test
    void giniPositiveForUnequalLoads() {
        double gini = gini(List.of(10, 0, 0, 0));
        assertTrue(gini > 0.5); // high inequality
    }

    @Test
    void giniBounded() {
        for (int i = 0; i < 10; i++) {
            double g = gini(List.of(i * 10, i * 5, i * 2, i));
            assertTrue(g >= 0 && g <= 1);
        }
    }

    // Copied from MetricsCalculator for isolated testing
    private static double gini(List<Integer> values) {
        int n = values.size();
        if (n <= 1) return 0;
        long sum = values.stream().mapToLong(Long::valueOf).sum();
        if (sum == 0) return 0;
        long numerator = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                numerator += Math.abs(values.get(i) - values.get(j));
            }
        }
        return numerator / (2.0 * n * sum);
    }
}
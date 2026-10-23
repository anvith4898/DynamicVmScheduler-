package com.college.cloudsim;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkloadGeneratorTest {

    @Test
    void sameSeedProducesSameLengths() {
        List<Long> a = WorkloadGenerator.generateLengths(10, 42L);
        List<Long> b = WorkloadGenerator.generateLengths(10, 42L);
        assertEquals(a, b);
    }

    @Test
    void differentSeedsProduceDifferentLengths() {
        List<Long> a = WorkloadGenerator.generateLengths(10, 42L);
        List<Long> b = WorkloadGenerator.generateLengths(10, 43L);
        assertNotEquals(a, b);
    }

    @Test
    void lengthsInConfiguredRange() {
        List<Long> lengths = WorkloadGenerator.generateLengths(100, 123L);
        for (long len : lengths) {
            assertTrue(len >= SimulationConfig.MIN_LENGTH_MI && len <= SimulationConfig.MAX_LENGTH_MI,
                "Length " + len + " out of range [" + SimulationConfig.MIN_LENGTH_MI + ", " + SimulationConfig.MAX_LENGTH_MI + "]");
        }
    }

    @Test
    void correctCount() {
        assertEquals(25, WorkloadGenerator.generateLengths(25, 1L).size());
        assertEquals(100, WorkloadGenerator.generateLengths(100, 1L).size());
        assertEquals(300, WorkloadGenerator.generateLengths(300, 1L).size());
    }

    @Test
    void invalidCountThrows() {
        assertThrows(IllegalArgumentException.class, () -> WorkloadGenerator.generateLengths(0, 1L));
        assertThrows(IllegalArgumentException.class, () -> WorkloadGenerator.generateLengths(-5, 1L));
    }
}
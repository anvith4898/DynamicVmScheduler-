package com.college.cloudsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates deterministic task workloads.
 *
 * <p>A workload is just a list of task lengths (in Million Instructions).
 * The same {@code (taskCount, seed)} always produces the same lengths, so
 * FCFS, Round Robin and Dynamic can be compared on exactly the same input.</p>
 */
public final class WorkloadGenerator {
    private WorkloadGenerator() {}

    /**
     * Generates {@code taskCount} task lengths uniformly distributed between
     * {@link SimulationConfig#MIN_LENGTH_MI} and {@link SimulationConfig#MAX_LENGTH_MI}.
     *
     * @param taskCount number of tasks to generate
     * @param seed fixed random seed for reproducibility
     * @return list of task lengths in MI, arrival order = list order
     */
    public static List<Long> generateLengths(int taskCount, long seed) {
        if (taskCount <= 0) {
            throw new IllegalArgumentException("taskCount must be positive: " + taskCount);
        }
        Random rng = new Random(seed);
        long range = SimulationConfig.MAX_LENGTH_MI - SimulationConfig.MIN_LENGTH_MI + 1;
        List<Long> lengths = new ArrayList<>(taskCount);
        for (int i = 0; i < taskCount; i++) {
            lengths.add(SimulationConfig.MIN_LENGTH_MI + (long) (rng.nextDouble() * range));
        }
        return lengths;
    }
}

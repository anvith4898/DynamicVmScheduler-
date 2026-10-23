package com.college.cloudsim;

/**
 * Single place holding the simulated cloud configuration.
 *
 * <p>Topology: 1 datacenter, 2 hosts, 4 heterogeneous VMs.
 * The VMs intentionally have different CPU capacities so that a
 * load-aware scheduler can outperform naive ones.</p>
 */
public final class SimulationConfig {
    private SimulationConfig() {}

    // ---- Datacenter / hosts ----
    public static final int HOST_COUNT = 2;
    public static final int HOST_PES = 8;
    public static final double HOST_MIPS_PER_PE = 10_000;
    public static final long HOST_RAM_MB = 32_768;
    public static final long HOST_BW_MBPS = 10_000;
    public static final long HOST_STORAGE_MB = 1_000_000;

    // ---- VMs (heterogeneous on purpose) ----
    /** Per-PE MIPS of each VM. VM0 is slow, VM3 is fast, VM1/VM2 tie (tests tie-breaking). */
    public static final double[] VM_MIPS = {1000, 2000, 2000, 3000};
    public static final int VM_PES = 2;
    public static final long VM_RAM_MB = 2048;
    public static final long VM_BW_MBPS = 2000;
    public static final long VM_SIZE_MB = 10_000;

    // ---- Cloudlets (tasks) ----
    public static final int CLOUDLET_PES = 1;
    public static final long CLOUDLET_FILE_SIZE = 300;
    public static final long CLOUDLET_OUTPUT_SIZE = 300;
    /** Task lengths in Million Instructions (MI), drawn uniformly from this range. */
    public static final long MIN_LENGTH_MI = 5_000;
    public static final long MAX_LENGTH_MI = 20_000;

    // ---- Experiments ----
    public static final long DEFAULT_SEED = 42L;
    public static final int[] WORKLOAD_SIZES = {25, 100, 300};

    public static int vmCount() {
        return VM_MIPS.length;
    }
}

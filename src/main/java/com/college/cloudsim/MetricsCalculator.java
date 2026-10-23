package com.college.cloudsim;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

/**
 * Runs a single scheduling scenario and computes performance metrics
 * from the actual simulation results.
 */
public final class MetricsCalculator {
    private MetricsCalculator() {}

    /**
     * Runs the simulation with the given scheduler and returns the computed metrics.
     * The scheduler assignment is enforced via {@link SchedulingBroker}.
     */
    public static SimulationMetrics run(Scheduler scheduler,
                                        List<Long> taskLengths,
                                        String workloadLabel) {
        return runDetailed(scheduler, taskLengths, workloadLabel).metrics();
    }

    /**
     * Runs the simulation and returns both aggregate metrics and per-task
     * execution details (used by the HTTP bridge / frontend).
     */
    public static DetailedResult runDetailed(Scheduler scheduler,
                                             List<Long> taskLengths,
                                             String workloadLabel) {
        SimulationEnvironment env = SimulationEnvironment.createWithScheduler(taskLengths, scheduler);

        // Run simulation
        env.simulation().start();

        // Collect finished cloudlets
        List<Cloudlet> finished = new ArrayList<>(env.broker().getCloudletFinishedList());
        finished.sort(Comparator.comparingDouble(Cloudlet::getFinishTime));

        SimulationMetrics metrics =
            computeMetrics(scheduler.name(), workloadLabel, taskLengths.size(), env.vms(), finished);

        List<TaskDetail> tasks = new ArrayList<>(finished.size());
        for (int i = 0; i < finished.size(); i++) {
            Cloudlet c = finished.get(i);
            Vm vm = c.getVm();
            int vmIndex = vm != null ? env.vms().indexOf(vm) : -1;
            double wait = c.getStartTime() - c.getDcArrivalTime();
            double response = c.getFinishTime() - c.getDcArrivalTime();
            double exec = c.getFinishTime() - c.getStartTime();
            tasks.add(new TaskDetail(i, c.getId(), c.getLength(),
                vmIndex, wait, response, exec,
                c.getStartTime(), c.getFinishTime()));
        }

        return new DetailedResult(metrics, tasks);
    }

    private static SimulationMetrics computeMetrics(String algoName,
                                                    String workloadLabel,
                                                    int taskCount,
                                                    List<Vm> vms,
                                                    List<Cloudlet> finished) {
        if (finished.isEmpty()) {
            return new SimulationMetrics(algoName, workloadLabel, taskCount, vms.size(),
                0, 0, 0, 0, 0, Map.of(), 0);
        }

        // Makespan = max finish time
        double makespan = finished.stream()
            .mapToDouble(Cloudlet::getFinishTime)
            .max().orElse(0);

        // Waiting time = startTime - arrivalTime (dcArrivalTime)
        // CloudSim Plus provides getStartTime() which is when execution began,
        // and getDcArrivalTime() which is when the cloudlet arrived at the datacenter.
        double totalWait = 0;
        double totalResponse = 0;
        for (Cloudlet c : finished) {
            double wait = c.getStartTime() - c.getDcArrivalTime();
            double response = c.getFinishTime() - c.getDcArrivalTime();
            totalWait += wait;
            totalResponse += response;
        }
        double avgWait = totalWait / finished.size();
        double avgResponse = totalResponse / finished.size();

        // Throughput = tasks completed per simulation time unit
        double throughput = makespan > 0 ? finished.size() / makespan : 0;

        // CPU utilization: sum of (busy_time) / (makespan * total MIPS capacity)
        // Busy time per VM = sum of execution times of its cloudlets
        Map<Vm, Double> vmBusyTime = new LinkedHashMap<>();
        for (Vm vm : vms) {
            vmBusyTime.put(vm, 0.0);
        }
        for (Cloudlet c : finished) {
            Vm vm = c.getVm();
            if (vm != null) {
                double execTime = c.getFinishTime() - c.getStartTime();
                vmBusyTime.merge(vm, execTime, Double::sum);
            }
        }

        double totalMipsCapacity = vms.stream()
            .mapToDouble(vm -> vm.getMips() * vm.getPesNumber())
            .sum();
        double totalBusyMips = 0;
        for (Map.Entry<Vm, Double> e : vmBusyTime.entrySet()) {
            double mips = e.getKey().getMips() * e.getKey().getPesNumber();
            totalBusyMips += e.getValue() * mips;
        }
        double cpuUtilization = (makespan * totalMipsCapacity) > 0
            ? totalBusyMips / (makespan * totalMipsCapacity) : 0;

        // Workload distribution: Gini coefficient over tasks-per-VM
        Map<Vm, Integer> tasksPerVm = new LinkedHashMap<>();
        for (Vm vm : vms) {
            tasksPerVm.put(vm, 0);
        }
        for (Cloudlet c : finished) {
            Vm vm = c.getVm();
            if (vm != null) {
                tasksPerVm.merge(vm, 1, Integer::sum);
            }
        }
        List<Integer> loads = new ArrayList<>(tasksPerVm.values());
        Collections.sort(loads);
        double gini = computeGini(loads);

        return new SimulationMetrics(
            algoName, workloadLabel, taskCount, vms.size(),
            makespan, avgWait, avgResponse, throughput, cpuUtilization,
            tasksPerVm, gini
        );
    }

    /**
     * Gini coefficient of a non-negative list. 0 = perfect equality, 1 = max inequality.
     */
    private static double computeGini(List<Integer> values) {
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

    /**
     * Appends a single result row to the CSV file.
     */
    public static void appendCsv(SimulationMetrics m, String csvPath) throws IOException {
        Path path = Paths.get(csvPath);
        boolean writeHeader = !Files.exists(path);
        try (BufferedWriter w = Files.newBufferedWriter(path, java.nio.charset.StandardCharsets.UTF_8,
                writeHeader ? java.nio.file.StandardOpenOption.CREATE : java.nio.file.StandardOpenOption.APPEND)) {
            if (writeHeader) {
                w.write("algorithm,workload_label,task_count,vm_count,makespan,avg_waiting_time,avg_response_time,throughput,cpu_utilization,gini_coefficient,timestamp\n");
            }
            String ts = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            w.write(String.format("%s,%s,%d,%d,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%s%n",
                m.algoName, m.workloadLabel, m.taskCount, m.vmCount,
                m.makespan, m.avgWait, m.avgResponse, m.throughput, m.cpuUtilization, m.gini, ts));
        }
    }

    /** Immutable record of all metrics for one simulation run. */
    public static final class SimulationMetrics {
        public final String algoName;
        public final String workloadLabel;
        public final int taskCount;
        public final int vmCount;
        public final double makespan;
        public final double avgWait;
        public final double avgResponse;
        public final double throughput;
        public final double cpuUtilization;
        public final Map<Vm, Integer> tasksPerVm;
        public final double gini;

        public SimulationMetrics(String algoName, String workloadLabel, int taskCount, int vmCount,
                                 double makespan, double avgWait, double avgResponse,
                                 double throughput, double cpuUtilization,
                                 Map<Vm, Integer> tasksPerVm, double gini) {
            this.algoName = algoName;
            this.workloadLabel = workloadLabel;
            this.taskCount = taskCount;
            this.vmCount = vmCount;
            this.makespan = makespan;
            this.avgWait = avgWait;
            this.avgResponse = avgResponse;
            this.throughput = throughput;
            this.cpuUtilization = cpuUtilization;
            this.tasksPerVm = tasksPerVm;
            this.gini = gini;
        }

        public void printSummary() {
            System.out.printf("%n=== %s | %s (%d tasks, %d VMs) ===%n",
                algoName, workloadLabel, taskCount, vmCount);
            System.out.printf("  Makespan:           %.2f%n", makespan);
            System.out.printf("  Avg Waiting Time:   %.2f%n", avgWait);
            System.out.printf("  Avg Response Time:  %.2f%n", avgResponse);
            System.out.printf("  Throughput:         %.6f tasks/unit%n", throughput);
            System.out.printf("  CPU Utilization:    %.2f%%%n", cpuUtilization * 100);
            System.out.printf("  Gini (load bal):    %.4f%n", gini);
            System.out.print("  Tasks per VM:       ");
            for (Map.Entry<Vm, Integer> e : tasksPerVm.entrySet()) {
                System.out.printf("VM%d=%d ", e.getKey().getId(), e.getValue());
            }
            System.out.println();
        }
    }

    /** Per-task execution detail for JSON export to the frontend bridge. */
    public static final class TaskDetail {
        public final int index;
        public final long cloudletId;
        public final long lengthMi;
        public final int vmIndex;
        public final double waitingTime;
        public final double responseTime;
        public final double executionTime;
        public final double startTime;
        public final double finishTime;

        public TaskDetail(int index, long cloudletId, long lengthMi, int vmIndex,
                          double waitingTime, double responseTime, double executionTime,
                          double startTime, double finishTime) {
            this.index = index;
            this.cloudletId = cloudletId;
            this.lengthMi = lengthMi;
            this.vmIndex = vmIndex;
            this.waitingTime = waitingTime;
            this.responseTime = responseTime;
            this.executionTime = executionTime;
            this.startTime = startTime;
            this.finishTime = finishTime;
        }
    }

    /** Aggregate metrics plus per-task details for one run. */
    public static final class DetailedResult {
        private final SimulationMetrics metrics;
        private final List<TaskDetail> tasks;

        public DetailedResult(SimulationMetrics metrics, List<TaskDetail> tasks) {
            this.metrics = metrics;
            this.tasks = tasks;
        }

        public SimulationMetrics metrics() {
            return metrics;
        }

        public List<TaskDetail> tasks() {
            return tasks;
        }
    }
}
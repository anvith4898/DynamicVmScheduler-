package com.college.cloudsim;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import org.cloudsimplus.cloudlets.Cloudlet;

/**
 * Main entry point: runs single algorithm or comparison experiment.
 * Includes a 'test' command to debug simulation execution.
 */
public final class App {
    private static final String RESULTS_CSV = "results/results.csv";

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String cmd = args[0].toLowerCase();
        Paths.get("results").toFile().mkdirs();

        if ("test".equals(cmd)) {
            // Minimal test: run simulation and report results
            List<Long> lengths = WorkloadGenerator.generateLengths(25, 42L);
            Scheduler scheduler = new DynamicScheduler();
            SimulationEnvironment env = SimulationEnvironment.createWithScheduler(lengths, scheduler);
            System.out.println("Running simulation...");
            env.simulation().start();
            List<Cloudlet> finished = new ArrayList<>(env.broker().getCloudletFinishedList());
            System.out.println("Finished cloudets: " + finished.size());
            System.out.println("Simulation clock: " + env.simulation().clock());
            for (Cloudlet c : finished) {
                System.out.println("Cloudlet " + c.getId() + ": finish=" + c.getFinishTime() + ", start=" + c.getStartTime() + ", vm=" + c.getVm().getId() + ", wait=" + (c.getStartTime() - c.getDcArrivalTime()));
            }
            return;
        }

        if (args.length == 0) {
            printUsage();
            return;
        }

        if ("compare".equals(cmd)) {
            runComparison();
        } else if ("pptx".equals(cmd)) {
            PptxGenerator.main(new String[0]);
        } else if ("serve".equals(cmd)) {
            int port = args.length >= 2 ? Integer.parseInt(args[1]) : 8080;
            try {
                SimulationServer.start(port);
            } catch (IOException e) {
                System.err.println("Failed to start bridge server: " + e.getMessage());
            }
        } else if (cmd.equals("fcfs") || cmd.equals("rr") || cmd.equals("dynamic")) {
            int taskCount = args.length >= 2 ? Integer.parseInt(args[1]) : 100;
            runSingle(cmd, taskCount);
        } else {
            System.err.println("Unknown command: " + cmd);
            printUsage();
        }
    }

    private static void runSingle(String algoKey, int taskCount) {
        List<Long> lengths = WorkloadGenerator.generateLengths(taskCount, SimulationConfig.DEFAULT_SEED);
        Scheduler scheduler = switch (algoKey) {
            case "fcfs" -> new FcfsScheduler();
            case "rr" -> new RoundRobinScheduler();
            case "dynamic" -> new DynamicScheduler();
            default -> throw new IllegalStateException("unknown: " + algoKey);
        };

        String label = "TASKS_" + taskCount;
        System.out.printf("Running %s with %d tasks (seed=%d)...%n",
            scheduler.name(), taskCount, SimulationConfig.DEFAULT_SEED);

        var metrics = MetricsCalculator.run(scheduler, lengths, label);
        metrics.printSummary();

        try {
            MetricsCalculator.appendCsv(metrics, RESULTS_CSV);
            System.out.printf("Appended to %s%n", RESULTS_CSV);
        } catch (IOException e) {
            System.err.println("Failed to write CSV: " + e.getMessage());
        }
    }

    private static void runComparison() throws IOException {
        System.out.println("=== COMPARISON EXPERIMENT ===");
        System.out.println("Algorithms: FCFS, ROUND_ROBIN, DYNAMIC");
        System.out.println("Workloads: " + java.util.Arrays.toString(SimulationConfig.WORKLOAD_SIZES));
        System.out.println("Seed: " + SimulationConfig.DEFAULT_SEED);
        System.out.println();

        for (int taskCount : SimulationConfig.WORKLOAD_SIZES) {
            List<Long> lengths = WorkloadGenerator.generateLengths(taskCount, SimulationConfig.DEFAULT_SEED);
            String label = "TASKS_" + taskCount;

            Scheduler[] schedulers = {
                new FcfsScheduler(),
                new RoundRobinScheduler(),
                new DynamicScheduler()
            };

            for (Scheduler s : schedulers) {
                var m = MetricsCalculator.run(s, lengths, label);
                m.printSummary();
                MetricsCalculator.appendCsv(m, RESULTS_CSV);
            }
            System.out.println();
        }

        printComparisonTable();
    }

    private static void printComparisonTable() {
        try {
            List<String> lines = java.nio.file.Files.readAllLines(Paths.get(RESULTS_CSV), java.nio.charset.StandardCharsets.UTF_8);
            if (lines.size() <= 1) {
                System.out.println("No results yet.");
                return;
            }
            System.out.println("=== COMPARISON TABLE ===");
            System.out.printf("%-12s %-12s %8s %8s %8s %8s %8s %8s%n",
                "Algorithm", "Workload", "Makespan", "Wait", "Response", "Throughput", "CPU%", "Gini");
            for (int i = 1; i < lines.size(); i++) {
                String[] p = lines.get(i).split(",");
                if (p.length >= 10) {
                    System.out.printf("%-12s %-12s %8.2f %8.2f %8.2f %8.6f %7.1f%% %8.4f%n",
                        p[0], p[1], Double.parseDouble(p[4]), Double.parseDouble(p[5]),
                        Double.parseDouble(p[6]), Double.parseDouble(p[7]),
                        Double.parseDouble(p[8]) * 100, Double.parseDouble(p[9]));
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read CSV for table: " + e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("""
            Usage:
              mvn compile exec:java -Dexec.args="fcfs [taskCount]"
              mvn compile exec:java -Dexec.args="rr [taskCount]"
              mvn compile exec:java -Dexec.args="dynamic [taskCount]"
              mvn compile exec:java -Dexec.args="compare"
              mvn compile exec:java -Dexec.args="test"
              mvn compile exec:java -Dexec.args="serve [port]"

            Examples:
              mvn compile exec:java -Dexec.args="fcfs 25"
              mvn compile exec:java -Dexec.args="compare"
              mvn compile exec:java -Dexec.args="test"
              mvn compile exec:java -Dexec.args="serve 8080"
            """);
    }
}
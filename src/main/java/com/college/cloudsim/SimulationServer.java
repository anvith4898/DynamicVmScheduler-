package com.college.cloudsim;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import org.cloudsimplus.vms.Vm;

/**
 * Minimal HTTP bridge exposing the CloudSim Plus schedulers to the
 * Next.js frontend. Uses only the JDK built-in HttpServer (no extra deps).
 *
 * <p>Endpoints:
 * <ul>
 *   <li>GET  /api/health — server + simulation config info</li>
 *   <li>POST /api/simulate — {algorithm, taskCount?, seed?, lengths?[]} run one scheduler</li>
 *   <li>POST /api/compare — {taskCounts?[], seed?} run FCFS+RR+DYNAMIC on same workloads</li>
 * </ul>
 *
 * <p>Run via: {@code mvn compile exec:java -Dexec.args="serve 8080"}
 * or {@code java -cp ... com.college.cloudsim.App serve 8080}.</p>
 */
public final class SimulationServer {
    private SimulationServer() {}

    public static void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/health", SimulationServer::handleHealth);
        server.createContext("/api/simulate", SimulationServer::handleSimulate);
        server.createContext("/api/compare", SimulationServer::handleCompare);
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.printf("CloudSim bridge server listening on http://localhost:%d%n", port);
        System.out.println("Endpoints: GET /api/health, POST /api/simulate, POST /api/compare");
    }

    // ---------- handlers ----------

    private static void handleHealth(HttpExchange ex) throws IOException {
        if (!allowCors(ex)) return;
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 405, "{\"error\":\"Use GET\"}");
            return;
        }
        StringBuilder vmMips = new StringBuilder("[");
        for (int i = 0; i < SimulationConfig.VM_MIPS.length; i++) {
            if (i > 0) vmMips.append(",");
            vmMips.append(fmt(SimulationConfig.VM_MIPS[i]));
        }
        vmMips.append("]");
        StringBuilder sizes = new StringBuilder("[");
        for (int i = 0; i < SimulationConfig.WORKLOAD_SIZES.length; i++) {
            if (i > 0) sizes.append(",");
            sizes.append(SimulationConfig.WORKLOAD_SIZES[i]);
        }
        sizes.append("]");
        send(ex, 200, "{"
            + "\"status\":\"ok\","
            + "\"framework\":\"CloudSim Plus\","
            + "\"java\":\"" + System.getProperty("java.version") + "\","
            + "\"vmMips\":" + vmMips + ","
            + "\"vmPes\":" + SimulationConfig.VM_PES + ","
            + "\"vmRamMb\":" + SimulationConfig.VM_RAM_MB + ","
            + "\"hostCount\":" + SimulationConfig.HOST_COUNT + ","
            + "\"workloadSizes\":" + sizes + ","
            + "\"defaultSeed\":" + SimulationConfig.DEFAULT_SEED
            + "}");
    }

    private static void handleSimulate(HttpExchange ex) throws IOException {
        if (!allowCors(ex)) return;
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 405, "{\"error\":\"Use POST\"}");
            return;
        }
        String body = readBody(ex);
        String algoKey = strField(body, "algorithm", "dynamic").toLowerCase();
        Scheduler scheduler = schedulerFor(algoKey);
        if (scheduler == null) {
            send(ex, 400, "{\"error\":\"Unknown algorithm. Use fcfs, rr or dynamic.\"}");
            return;
        }
        List<Long> lengths = lengthsField(body);
        int taskCount = intField(body, "taskCount", lengths.isEmpty() ? 25 : lengths.size());
        long seed = longField(body, "seed", SimulationConfig.DEFAULT_SEED);
        if (lengths.isEmpty()) {
            if (taskCount <= 0 || taskCount > 5000) {
                send(ex, 400, "{\"error\":\"taskCount must be 1..5000\"}");
                return;
            }
            lengths = WorkloadGenerator.generateLengths(taskCount, seed);
        }
        String label = "TASKS_" + lengths.size();
        try {
            MetricsCalculator.DetailedResult detailed =
                MetricsCalculator.runDetailed(scheduler, lengths, label);
            send(ex, 200, detailedJson(scheduler.name(), label, seed, lengths, detailed));
        } catch (Exception e) {
            e.printStackTrace();
            send(ex, 500, "{\"error\":\"Simulation failed: " + esc(String.valueOf(e.getMessage())) + "\"}");
        }
    }

    private static void handleCompare(HttpExchange ex) throws IOException {
        if (!allowCors(ex)) return;
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 405, "{\"error\":\"Use POST\"}");
            return;
        }
        String body = readBody(ex);
        List<Integer> counts = intArrayField(body, "taskCounts");
        if (counts.isEmpty()) {
            counts = new ArrayList<>();
            for (int s : SimulationConfig.WORKLOAD_SIZES) counts.add(s);
        }
        long seed = longField(body, "seed", SimulationConfig.DEFAULT_SEED);
        Scheduler[] schedulers = {new FcfsScheduler(), new RoundRobinScheduler(), new DynamicScheduler()};
        StringBuilder out = new StringBuilder("{\"seed\":" + seed + ",\"results\":[");
        boolean first = true;
        try {
            for (int taskCount : counts) {
                if (taskCount <= 0 || taskCount > 5000) continue;
                List<Long> lengths = WorkloadGenerator.generateLengths(taskCount, seed);
                String label = "TASKS_" + taskCount;
                for (Scheduler s : schedulers) {
                    MetricsCalculator.DetailedResult d =
                        MetricsCalculator.runDetailed(s, lengths, label);
                    if (!first) out.append(",");
                    first = false;
                    out.append(metricsJson(s.name(), label, taskCount, seed, d.metrics()));
                }
            }
            out.append("]}");
            send(ex, 200, out.toString());
        } catch (Exception e) {
            e.printStackTrace();
            send(ex, 500, "{\"error\":\"Comparison failed: " + esc(String.valueOf(e.getMessage())) + "\"}");
        }
    }

    // ---------- JSON builders ----------

    private static String detailedJson(String algo, String label, long seed,
                                       List<Long> lengths,
                                       MetricsCalculator.DetailedResult detailed) {
        MetricsCalculator.SimulationMetrics m = detailed.metrics();
        StringBuilder sb = new StringBuilder();
        sb.append("{\"seed\":").append(seed).append(",");
        sb.append(metricsJsonInner(algo, label, m)).append(",");
        sb.append("\"lengths\":[");
        for (int i = 0; i < lengths.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(lengths.get(i));
        }
        sb.append("],\"tasks\":[");
        List<MetricsCalculator.TaskDetail> tasks = detailed.tasks();
        for (int i = 0; i < tasks.size(); i++) {
            MetricsCalculator.TaskDetail t = tasks.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"index\":").append(t.index)
              .append(",\"cloudletId\":").append(t.cloudletId)
              .append(",\"lengthMi\":").append(t.lengthMi)
              .append(",\"vmIndex\":").append(t.vmIndex)
              .append(",\"waitingTime\":").append(fmt(t.waitingTime))
              .append(",\"responseTime\":").append(fmt(t.responseTime))
              .append(",\"executionTime\":").append(fmt(t.executionTime))
              .append(",\"startTime\":").append(fmt(t.startTime))
              .append(",\"finishTime\":").append(fmt(t.finishTime))
              .append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private static String metricsJson(String algo, String label, int taskCount, long seed,
                                      MetricsCalculator.SimulationMetrics m) {
        return "{\"seed\":" + seed + "," + metricsJsonInner(algo, label, m) + "}";
    }

    private static String metricsJsonInner(String algo, String label, MetricsCalculator.SimulationMetrics m) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"algorithm\":\"").append(esc(algo)).append("\",")
          .append("\"workloadLabel\":\"").append(esc(label)).append("\",")
          .append("\"taskCount\":").append(m.taskCount).append(",")
          .append("\"vmCount\":").append(m.vmCount).append(",")
          .append("\"makespan\":").append(fmt(m.makespan)).append(",")
          .append("\"avgWaitingTime\":").append(fmt(m.avgWait)).append(",")
          .append("\"avgResponseTime\":").append(fmt(m.avgResponse)).append(",")
          .append("\"throughput\":").append(fmt(m.throughput)).append(",")
          .append("\"cpuUtilization\":").append(fmt(m.cpuUtilization)).append(",")
          .append("\"gini\":").append(fmt(m.gini)).append(",")
          .append("\"tasksPerVm\":[");
        boolean first = true;
        for (Map.Entry<Vm, Integer> e : m.tasksPerVm.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("{\"vmId\":").append(e.getKey().getId())
              .append(",\"tasks\":").append(e.getValue()).append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    // ---------- tiny JSON request parser (flat shapes only) ----------

    private static String strField(String json, String key, String def) {
        String pat = "\"" + key + "\"";
        int i = json.indexOf(pat);
        if (i < 0) return def;
        int c = json.indexOf(':', i + pat.length());
        if (c < 0) return def;
        int q1 = json.indexOf('"', c + 1);
        if (q1 < 0) return def;
        int q2 = json.indexOf('"', q1 + 1);
        if (q2 < 0) return def;
        return json.substring(q1 + 1, q2);
    }

    private static int intField(String json, String key, int def) {
        return (int) longField(json, key, def);
    }

    private static long longField(String json, String key, long def) {
        String pat = "\"" + key + "\"";
        int i = json.indexOf(pat);
        if (i < 0) return def;
        int c = json.indexOf(':', i + pat.length());
        if (c < 0) return def;
        int s = c + 1;
        while (s < json.length() && (json.charAt(s) == ' ' || json.charAt(s) == '"')) s++;
        int e = s;
        while (e < json.length() && (Character.isDigit(json.charAt(e)) || json.charAt(e) == '-')) e++;
        if (e <= s) return def;
        try {
            return Long.parseLong(json.substring(s, e));
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    private static List<Long> lengthsField(String json) {
        List<Long> out = new ArrayList<>();
        String pat = "\"lengths\"";
        int i = json.indexOf(pat);
        if (i < 0) return out;
        int b1 = json.indexOf('[', i + pat.length());
        int b2 = json.indexOf(']', b1 < 0 ? 0 : b1);
        if (b1 < 0 || b2 < 0) return out;
        for (String part : json.substring(b1 + 1, b2).split(",")) {
            try {
                long v = Long.parseLong(part.trim());
                if (v > 0) out.add(v);
            } catch (NumberFormatException ignored) {
            }
        }
        return out;
    }

    private static List<Integer> intArrayField(String json, String key) {
        List<Integer> out = new ArrayList<>();
        String pat = "\"" + key + "\"";
        int i = json.indexOf(pat);
        if (i < 0) return out;
        int b1 = json.indexOf('[', i + pat.length());
        int b2 = b1 < 0 ? -1 : json.indexOf(']', b1);
        if (b1 < 0 || b2 < 0) return out;
        for (String part : json.substring(b1 + 1, b2).split(",")) {
            try {
                out.add(Integer.parseInt(part.trim()));
            } catch (NumberFormatException ignored) {
            }
        }
        return out;
    }

    private static Scheduler schedulerFor(String key) {
        return switch (key) {
            case "fcfs" -> new FcfsScheduler();
            case "rr", "round_robin", "round-robin" -> new RoundRobinScheduler();
            case "dynamic", "dts" -> new DynamicScheduler();
            default -> null;
        };
    }

    // ---------- http helpers ----------

    private static boolean allowCors(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 204, "");
            return false;
        }
        return true;
    }

    private static String readBody(HttpExchange ex) throws IOException {
        byte[] bytes = ex.getRequestBody().readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static void send(HttpExchange ex, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String fmt(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "0";
        return String.format(Locale.US, "%.6f", v);
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

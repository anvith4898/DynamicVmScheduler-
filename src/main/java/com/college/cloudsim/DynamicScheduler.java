package com.college.cloudsim;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

/**
 * Dynamic scheduler: assigns each task to the VM with the lowest
 * predicted completion time (waiting + execution).
 *
 * <p>Algorithm (per incoming task in arrival order):
 * <ol>
 *   <li>For each VM, compute:
 *       <pre>
 *       predictedFinish = currentLoad[vm] + executionTime(task, vm)
 *       </pre>
 *       where {@code currentLoad[vm]} is the sum of execution times of
 *       all tasks previously assigned to that VM.</li>
 *   <li>Pick the VM with the smallest {@code predictedFinish}.
 *       Tie-break by lower VM index (deterministic).</li>
 *   <li>Assign the task to that VM and update {@code currentLoad[vm]}.</li>
 * </ol>
 *
 * Execution time uses the standard MIPS model:
 * {@code execTime = taskLength_MI / (vmMips * vmPes)}.
 * This matches CloudSim's {@code CloudletSchedulerSpaceShared} where
 * a cloudlet runs on all PEs of its VM concurrently.
 */
public final class DynamicScheduler implements Scheduler {
    @Override
    public String name() {
        return "DYNAMIC";
    }

    @Override
    public Map<Cloudlet, Vm> schedule(List<Cloudlet> cloudlets, List<Vm> vms) {
        if (cloudlets.isEmpty() || vms.isEmpty()) {
            return Map.of();
        }

        int n = vms.size();
        // running total of execution time already assigned to each VM
        double[] load = new double[n];
        // Use IdentityHashMap because CloudletSimple.equals() is based on ID,
        // and all pre-submission cloudlets have ID -1 (would collide in regular maps).
        Map<Cloudlet, Vm> assignment = new IdentityHashMap<>(cloudlets.size());

        System.out.printf("    [DYN] Scheduling %d cloudlets across %d VMs%n", cloudlets.size(), n);

        for (Cloudlet c : cloudlets) {
            long length = c.getLength(); // MI
            int bestIdx = -1;
            double bestFinish = Double.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                Vm vm = vms.get(i);
                double mipsPerPe = vm.getMips();
                long pes = vm.getPesNumber();
                double execTime = length / (mipsPerPe * pes); // same unit as CloudSim's internal clock
                double predictedFinish = load[i] + execTime;

                if (predictedFinish < bestFinish ||
                    (Double.compare(predictedFinish, bestFinish) == 0 && i < bestIdx)) {
                    bestFinish = predictedFinish;
                    bestIdx = i;
                }
            }

            Vm chosen = vms.get(bestIdx);
            assignment.put(c, chosen);
            // accumulate load for subsequent tasks
            double mipsPerPe = chosen.getMips();
            double execTime = length / (mipsPerPe * chosen.getPesNumber());
            load[bestIdx] += execTime;
            System.out.printf("      [DYN] Cloudlet %d (len=%d) -> Vm %d (load=%.4f)%n",
                c.getId(), length, chosen.getId(), load[bestIdx]);
        }

        return assignment;
    }
}
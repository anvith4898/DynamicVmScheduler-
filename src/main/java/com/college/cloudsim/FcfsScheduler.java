package com.college.cloudsim;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

/**
 * First Come, First Served: every task goes to the first VM.
 * Simple baseline that intentionally ignores capacity differences.
 */
public final class FcfsScheduler implements Scheduler {
    @Override
    public String name() {
        return "FCFS";
    }

    @Override
    public Map<Cloudlet, Vm> schedule(List<Cloudlet> cloudlets, List<Vm> vms) {
        if (cloudlets.isEmpty() || vms.isEmpty()) {
            return Map.of();
        }
        Vm target = vms.get(0);
        // Use IdentityHashMap because CloudletSimple.equals() is based on ID,
        // and all pre-submission cloudlets have ID -1 (would collide in regular maps).
        Map<Cloudlet, Vm> m = new IdentityHashMap<>(cloudlets.size());
        for (Cloudlet c : cloudlets) {
            m.put(c, target);
        }
        return m;
    }
}
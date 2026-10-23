package com.college.cloudsim;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

/**
 * Round Robin: tasks cycle through VMs in index order.
 */
public final class RoundRobinScheduler implements Scheduler {
    @Override
    public String name() {
        return "ROUND_ROBIN";
    }

    @Override
    public Map<Cloudlet, Vm> schedule(List<Cloudlet> cloudlets, List<Vm> vms) {
        if (cloudlets.isEmpty() || vms.isEmpty()) {
            return Map.of();
        }
        int n = vms.size();
        // Use IdentityHashMap because CloudletSimple.equals() is based on ID,
        // and all pre-submission cloudlets have ID -1 (would collide in regular maps).
        Map<Cloudlet, Vm> m = new IdentityHashMap<>(cloudlets.size());
        for (int i = 0; i < cloudlets.size(); i++) {
            m.put(cloudlets.get(i), vms.get(i % n));
        }
        return m;
    }
}
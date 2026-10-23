package com.college.cloudsim;

import java.util.List;
import java.util.Map;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

/**
 * Assigns every cloudlet (task) to exactly one VM before the simulation runs.
 * Implementations must be deterministic for a fixed input order.
 */
public interface Scheduler {
    /** Short code used in CSV files and console output (e.g. "FCFS"). */
    String name();

    /**
     * Maps each cloudlet to the VM that should execute it.
     *
     * @param cloudlets tasks in arrival order
     * @param vms available VMs (index in this list = VM position used for tie-breaking)
     * @return assignment of every cloudlet to a VM
     */
    Map<Cloudlet, Vm> schedule(List<Cloudlet> cloudlets, List<Vm> vms);
}

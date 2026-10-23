package com.college.cloudsim;

import java.util.List;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerTest {

    private static List<Vm> makeVms() {
        Vm v0 = new VmSimple(1000, 2); // 1000 MIPS * 2 PE = 2000 total
        Vm v1 = new VmSimple(2000, 2); // 2000 MIPS * 2 PE = 4000 total
        Vm v2 = new VmSimple(2000, 2); // same as v1 (tests tie-breaking)
        Vm v3 = new VmSimple(3000, 2); // 3000 MIPS * 2 PE = 6000 total
        return List.of(v0, v1, v2, v3);
    }

    private static List<Cloudlet> makeCloudlets() {
        UtilizationModelFull util = new UtilizationModelFull();
        // 4 cloudlets with varying lengths
        return List.of(
            new CloudletSimple(10_000, 1, util),
            new CloudletSimple(20_000, 1, util),
            new CloudletSimple(5_000, 1, util),
            new CloudletSimple(15_000, 1, util)
        );
    }

    @Test
    void fcfsAssignsAllToFirstVm() {
        Scheduler s = new FcfsScheduler();
        List<Cloudlet> cl = makeCloudlets();
        List<Vm> vms = makeVms();
        var assign = s.schedule(cl, vms);

        assertEquals(4, assign.size());
        for (Vm vm : assign.values()) {
            assertSame(vms.get(0), vm, "FCFS should always pick the first VM in the list");
        }
    }

    @Test
    void roundRobinCyclesThroughVms() {
        Scheduler s = new RoundRobinScheduler();
        List<Cloudlet> cl = makeCloudlets();
        List<Vm> vms = makeVms();
        var assign = s.schedule(cl, vms);

        assertEquals(4, assign.size());
        // 4 cloudlets, 4 VMs -> each gets one
        assertSame(vms.get(0), assign.get(cl.get(0)));
        assertSame(vms.get(1), assign.get(cl.get(1)));
        assertSame(vms.get(2), assign.get(cl.get(2)));
        assertSame(vms.get(3), assign.get(cl.get(3)));
    }

    @Test
    void roundRobinWrapsWithMoreTasksThanVms() {
        Scheduler s = new RoundRobinScheduler();
        UtilizationModelFull util = new UtilizationModelFull();
        // 6 cloudlets, 4 VMs -> indices 0,1,2,3,0,1
        List<Cloudlet> cl = List.of(
            new CloudletSimple(10_000, 1, util),
            new CloudletSimple(10_000, 1, util),
            new CloudletSimple(10_000, 1, util),
            new CloudletSimple(10_000, 1, util),
            new CloudletSimple(10_000, 1, util),
            new CloudletSimple(10_000, 1, util)
        );
        List<Vm> vms = makeVms();
        var assign = s.schedule(cl, vms);

        assertSame(vms.get(0), assign.get(cl.get(0)));
        assertSame(vms.get(1), assign.get(cl.get(1)));
        assertSame(vms.get(2), assign.get(cl.get(2)));
        assertSame(vms.get(3), assign.get(cl.get(3)));
        assertSame(vms.get(0), assign.get(cl.get(4)));
        assertSame(vms.get(1), assign.get(cl.get(5)));
    }

    @Test
    void dynamicPrefersLessLoadedVm() {
        Scheduler s = new DynamicScheduler();
        UtilizationModelFull util = new UtilizationModelFull();
        // VM0 (1000 MIPS) is slow, VM3 (3000 MIPS) is fast.
        // First task: all VMs have load 0 -> pick fastest (VM3, index 3).
        // Second task: VM3 has load > 0, others have 0 -> next fastest idle is VM1/VM2 (2000 MIPS).
        // Tie between VM1 and VM2 -> lower index (VM1, index 1) wins.
        List<Cloudlet> cl = List.of(
            new CloudletSimple(10_000, 1, util),
            new CloudletSimple(10_000, 1, util)
        );
        List<Vm> vms = makeVms();
        var assign = s.schedule(cl, vms);

        // First task goes to VM3 (fastest, index 3)
        assertSame(vms.get(3), assign.get(cl.get(0)),
            "First task should go to fastest idle VM (VM3)");

        // Second task: VM3 now has load, VM1 and VM2 are idle and equal speed.
        // VM1 (index 1) wins tie-break.
        assertSame(vms.get(1), assign.get(cl.get(1)),
            "Second task should go to VM1 (tie-break on index)");
    }

    @Test
    void dynamicTieBreaksByIndex() {
        // Two VMs with exactly same capacity
        Vm v0 = new VmSimple(2000, 2);
        Vm v1 = new VmSimple(2000, 2);
        List<Vm> vms = List.of(v0, v1);

        UtilizationModelFull util = new UtilizationModelFull();
        // Three tasks. First goes to VM0 (index 0), second to VM1, third back to VM0.
        List<Cloudlet> cl = List.of(
            new CloudletSimple(10_000, 1, util),
            new CloudletSimple(10_000, 1, util),
            new CloudletSimple(10_000, 1, util)
        );

        Scheduler s = new DynamicScheduler();
        var assign = s.schedule(cl, vms);

        assertSame(vms.get(0), assign.get(cl.get(0)));
        assertSame(vms.get(1), assign.get(cl.get(1)));
        assertSame(vms.get(0), assign.get(cl.get(2)));
    }
}
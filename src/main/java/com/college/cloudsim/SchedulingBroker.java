package com.college.cloudsim;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.vms.Vm;

/**
 * Broker that enforces a pre-computed cloudlet-to-VM assignment.
 *
 * <p>The assignment is keyed by cloudlet <b>object identity</b> (the exact
 * instances submitted to this broker), so it works regardless of when
 * CloudSim Plus assigns numeric cloudlet IDs. A legacy ID-based map is
 * kept as a fallback for assignments built from a different object set.</p>
 */
public final class SchedulingBroker extends DatacenterBrokerSimple {
    private final Map<Cloudlet, Vm> assignmentByInstance;
    private final Map<Long, Vm> assignmentById;

    public SchedulingBroker(CloudSimPlus simulation, Map<Cloudlet, Vm> assignment) {
        super(simulation);
        this.assignmentByInstance = assignment != null
            ? new IdentityHashMap<>(assignment)
            : Collections.emptyMap();
        this.assignmentById = Collections.emptyMap();
    }

    private SchedulingBroker(CloudSimPlus simulation, Map<Long, Vm> assignmentById, Object tag) {
        super(simulation);
        this.assignmentByInstance = Collections.emptyMap();
        this.assignmentById = assignmentById != null
            ? new HashMap<>(assignmentById)
            : Collections.emptyMap();
    }

    /** Legacy factory: assignment keyed by cloudlet ID. Prefer the instance-keyed constructor. */
    public static SchedulingBroker byIds(CloudSimPlus simulation, Map<Long, Vm> assignmentById) {
        return new SchedulingBroker(simulation, assignmentById, null);
    }

    public SchedulingBroker(CloudSimPlus simulation) {
        this(simulation, (Map<Cloudlet, Vm>) null);
    }

    @Override
    protected Vm defaultVmMapper(Cloudlet cloudlet) {
        Vm assigned = assignmentByInstance.get(cloudlet);
        if (assigned != null) {
            System.out.printf("  [BRIDGE] Cloudlet %d -> assigned Vm %d (instance match)%n",
                cloudlet.getId(), assigned.getId());
            return assigned;
        }
        if (!assignmentById.isEmpty()) {
            Vm byId = assignmentById.get(cloudlet.getId());
            if (byId != null) {
                System.out.printf("  [BRIDGE] Cloudlet %d -> assigned Vm %d (ID match)%n",
                    cloudlet.getId(), byId.getId());
                return byId;
            }
        }
        System.out.printf("  [BRIDGE] Cloudlet %d -> fallback to default%n", cloudlet.getId());
        // Fallback to parent's default (round-robin)
        return super.defaultVmMapper(cloudlet);
    }
}

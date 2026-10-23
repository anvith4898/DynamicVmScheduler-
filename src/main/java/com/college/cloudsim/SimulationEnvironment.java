package com.college.cloudsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;

/**
 * Builds a complete CloudSim simulation environment.
 *
 * <p>Structure:
 * <ul>
 *   <li>1 Datacenter</li>
 *   <li>{@link SimulationConfig#HOST_COUNT} Hosts (each {@link SimulationConfig#HOST_PES} PEs)</li>
 *   <li>{@link SimulationConfig#vmCount()} heterogeneous VMs</li>
 *   <li>Cloudlets with {@link SimulationConfig#CLOUDLET_PES} PE each</li>
 * </ul>
 *
 * <p>Use {@link #createWithScheduler(List, Scheduler)} so the scheduler's
 * assignment is actually enforced by the broker. The plain {@link #create(List)}
 * keeps the broker default mapping.</p>
 */
public final class SimulationEnvironment {
    private final CloudSimPlus simulation;
    private final DatacenterBrokerSimple broker;
    private final List<Vm> vms;
    private final List<Cloudlet> cloudlets;

    private SimulationEnvironment(CloudSimPlus simulation,
                                  DatacenterBrokerSimple broker,
                                  List<Vm> vms,
                                  List<Cloudlet> cloudlets) {
        this.simulation = simulation;
        this.broker = broker;
        this.vms = vms;
        this.cloudlets = cloudlets;
    }

    public static SimulationEnvironment create(List<Long> taskLengths) {
        return create(taskLengths, (Map<Long, Vm>) null);
    }

    /**
     * Legacy entry point: builds the environment and, when an ID-based
     * assignment is supplied, enforces it via {@link SchedulingBroker}.
     */
    public static SimulationEnvironment create(List<Long> taskLengths, Map<Long, Vm> assignmentById) {
        CloudSimPlus simulation = new CloudSimPlus();
        buildDatacenter(simulation);

        DatacenterBrokerSimple broker = (assignmentById != null && !assignmentById.isEmpty())
            ? SchedulingBroker.byIds(simulation, assignmentById)
            : new DatacenterBrokerSimple(simulation);

        List<Vm> vms = buildVms();
        broker.submitVmList(vms);

        List<Cloudlet> cloudlets = buildCloudlets(taskLengths);
        broker.submitCloudletList(cloudlets);

        return new SimulationEnvironment(simulation, broker, vms, cloudlets);
    }

    /**
     * Preferred entry point: the scheduler runs against the exact VM and
     * cloudlet instances that are submitted, and the resulting assignment
     * is enforced by a {@link SchedulingBroker} (identity-keyed, immune to
     * cloudlet-ID assignment timing).
     */
    public static SimulationEnvironment createWithScheduler(List<Long> taskLengths, Scheduler scheduler) {
        CloudSimPlus simulation = new CloudSimPlus();
        buildDatacenter(simulation);

        List<Vm> vms = buildVms();
        List<Cloudlet> cloudlets = buildCloudlets(taskLengths);

        Map<Cloudlet, Vm> assignment = scheduler.schedule(cloudlets, vms);
        System.out.printf("  [BRIDGE] cloudlets.size()=%d, vms.size()=%d%n", cloudlets.size(), vms.size());
        System.out.printf("  [BRIDGE] assignment.size()=%d%n", assignment.size());
        System.out.println("  [BRIDGE] Scheduler assignment:");
        for (Map.Entry<Cloudlet, Vm> e : assignment.entrySet()) {
            System.out.printf("    Cloudlet %d (len=%d) -> Vm %d%n",
                e.getKey().getId(), e.getKey().getLength(), e.getValue().getId());
        }

        SchedulingBroker broker = new SchedulingBroker(simulation, assignment);
        broker.submitVmList(vms);
        broker.submitCloudletList(cloudlets);

        return new SimulationEnvironment(simulation, broker, vms, cloudlets);
    }

    private static void buildDatacenter(CloudSimPlus simulation) {
        List<HostSimple> hosts = new ArrayList<>();
        for (int h = 0; h < SimulationConfig.HOST_COUNT; h++) {
            List<org.cloudsimplus.resources.Pe> pes = new ArrayList<>();
            for (int p = 0; p < SimulationConfig.HOST_PES; p++) {
                pes.add(new PeSimple(SimulationConfig.HOST_MIPS_PER_PE));
            }
            HostSimple host = new HostSimple(
                SimulationConfig.HOST_RAM_MB,
                SimulationConfig.HOST_BW_MBPS,
                SimulationConfig.HOST_STORAGE_MB,
                pes
            );
            hosts.add(host);
        }
        new DatacenterSimple(simulation, hosts);
    }

    private static List<Vm> buildVms() {
        List<Vm> vms = new ArrayList<>();
        for (int i = 0; i < SimulationConfig.vmCount(); i++) {
            Vm vm = new VmSimple(SimulationConfig.VM_MIPS[i], SimulationConfig.VM_PES);
            vm.setRam(SimulationConfig.VM_RAM_MB)
              .setBw(SimulationConfig.VM_BW_MBPS)
              .setSize(SimulationConfig.VM_SIZE_MB);
            // Use SpaceShared so cloudlets on a VM queue up — matches our waiting-time model
            vm.setCloudletScheduler(new org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerSpaceShared());
            vms.add(vm);
        }
        return vms;
    }

    private static List<Cloudlet> buildCloudlets(List<Long> taskLengths) {
        List<Cloudlet> cloudlets = new ArrayList<>();
        UtilizationModelFull util = new UtilizationModelFull();
        org.cloudsimplus.utilizationmodels.UtilizationModelDynamic resourceUtil =
            new org.cloudsimplus.utilizationmodels.UtilizationModelDynamic(0.25);
        for (int i = 0; i < taskLengths.size(); i++) {
            Cloudlet cl = new CloudletSimple(taskLengths.get(i), SimulationConfig.CLOUDLET_PES, util);
            cl.setUtilizationModelRam(resourceUtil);
            cl.setUtilizationModelBw(resourceUtil);
            cl.setFileSize(SimulationConfig.CLOUDLET_FILE_SIZE)
              .setOutputSize(SimulationConfig.CLOUDLET_OUTPUT_SIZE);
            cloudlets.add(cl);
        }
        return cloudlets;
    }

    public CloudSimPlus simulation() {
        return simulation;
    }

    public DatacenterBrokerSimple broker() {
        return broker;
    }

    public List<Vm> vms() {
        return vms;
    }

    public List<Cloudlet> cloudlets() {
        return cloudlets;
    }
}

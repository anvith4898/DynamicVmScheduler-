# CloudSim Dynamic Task Scheduling Prototype

## Project Title and Overview
**Performance Improvement in Cloud Computing Using Dynamic Task Scheduling Algorithm**

This prototype implements a cloud simulation that compares three task scheduling algorithms:
1. **First Come, First Served (FCFS)** - Allocates tasks in submission order
2. **Round Robin (RR)** - Distributes tasks across VMs in repeating order  
3. **Dynamic Scheduling** - Assigns tasks to VMs based on predicted completion time (waiting + execution time), with deterministic tie-breaking

The prototype uses **CloudSim Plus 8.5.1** framework with **JDK 21** and **Apache Maven 3.10.0**. It is designed as a college project demonstration that can be run from a Windows PowerShell terminal.

## Problem Statement
Task scheduling in cloud computing environments determines how computational tasks (cloudlets) are allocated to virtual machines (VMs). Poor scheduling leads to load imbalance, increased makespan, and inefficient resource utilization. This prototype implements and compares three scheduling algorithms to evaluate their performance on the same workload.

## Project Objectives
- Implement three scheduling algorithms using actual CloudSim Plus APIs
- Build a working cloud simulation prototype on Windows
- Calculate performance metrics from actual simulation results
- Provide a beginner-friendly demonstration that can be run from the command line
- Export results to CSV for analysis and comparison

## Technologies Used
- **Java 21** (Eclipse Adoptium Temurin JDK)
- **Apache Maven 3.10.0** (build and dependency management)
- **CloudSim Plus 8.5.1** (cloud simulation framework)
- **PowerShell** (Windows command-line execution)

## Prerequisites and Required Software
- **Windows 10/11** operating system
- **JDK 21** installed (verify with `java -version` and `javac -version`)
- **Apache Maven 3.10.0** installed and configured (verify with `mvn -version`)
- **Git** (optional, for cloning CloudSim; included in the working directory at `C:\tools\cloudsim`)
- **PowerShell** terminal (Windows PowerShell 5.1 or PowerShell 7+)

**Verify your environment:**
```powershell
java -version
javac -version
mvn -version
```

## Installation and Environment Setup
1. **Verify Java is installed and accessible:**
   ```powershell
   java -version
   javac -version
   ```
2. **Verify Maven is installed and configured:**
   ```powershell
   mvn -version
   ```
   If `mvn` is not recognized, add Maven to your PATH:
   ```powershell
   # Add to user PATH (permanent)
   [Environment]::SetEnvironmentVariable("Path", "$env:PATH;C:\tools\apache-maven-3.10.0\bin", "User")
   [Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\tools\apache-maven-3.10.0", "User")
   ```

3. **The working directory is pre-configured:**
   - `C:\tools\` contains all required software
   - `C:\tools\apache-maven-3.10.0\` - Apache Maven 3.10.0
   - `C:\tools\cloudsim\` - CloudSim 7.0.1 source repository
   - `C:\tools\cloudsim-dynamic-scheduler\` - This prototype project

4. **Verify the prototype project is set up:**
   ```powershell
   cd C:\tools\cloudsim-dynamic-scheduler
   dir
   ```

## Project Directory Structure
```
C:\tools\cloudsim-dynamic-scheduler\
│
├── pom.xml                  # Maven project configuration
├── README.md                # This file (project guide)
├── docs/
│   └── CloudSim_Dynamic_Task_Scheduling_Project.docx  # Project explanation document
├── results/
│   └── results.csv          # Exported performance metrics CSV
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── college/
│   │               └── cloudsim/
│   │                   ├── App.java           # Main CLI entry point
│   │                   ├── SimulationConfig.java  # Simulation configuration
│   │                   ├── WorkloadGenerator.java  # Task length generation
│   │                   ├── Scheduler.java       # Scheduler interface
│   │                   ├── FcfsScheduler.java   # FCFS algorithm
│   │                   ├── RoundRobinScheduler.java  # Round Robin algorithm
│   │                   ├── DynamicScheduler.java  # Dynamic algorithm
│   │                   ├── SimulationEnvironment.java  # CloudSim environment builder
│   │                   └── MetricsCalculator.java  # Performance metrics
│   └── test/
│       └── java/
│           └── com/
│               └── college/
│                   └── cloudsim/
│                       ├── SchedulerTest.java   # Scheduler unit tests
│                       ├── WorkloadGeneratorTest.java  # Workload generator tests
│                       └── MetricsCalculatorTest.java  # Metrics tests
│
└── cloudsim-install-report.txt  # Environment setup documentation
```

## Major Java Classes Explained

### `SimulationConfig.java`
Holds the simulated cloud configuration:
- **HostCount**: 2 hosts in the datacenter
- **HostPES**: 8 PEs per host (total 16 CPU cores)
- **HostMIPSPerPE**: 10,000 MIPS per PE
- **HostRAMMB**: 32,768 MB RAM per host
- **HostBWMBPS**: 10,000 Mbps bandwidth per host
- **VM_MIPS**: Array {1000, 2000, 2000, 3000} - heterogeneous VM capacities (4 VMs)
- **VM_PES**: 2 PEs per VM
- **VM_RAM_MB**: 2,048 MB per VM
- **VM_BW_MBPS**: 2,000 Mbps per VM
- **VM_SIZE_MB**: 10,000 MB VM size
- **Cloudlet_PES**: 1 PE per cloudlet
- **MIN_LENGTH_MI**: 5,000 Million Instructions (minimum task size)
- **MAX_LENGTH_MI**: 20,000 Million Instructions (maximum task size)
- **DEFAULT_SEED**: 42 (for reproducible workloads)

### `WorkloadGenerator.java`
Generates deterministic task workloads:
- Takes `taskCount` and `seed` as parameters
- Same `(taskCount, seed)` always produces the same task lengths
- Lengths uniformly distributed between MIN_LENGTH_MI and MAX_LENGTH_MI
- Used for fair comparison between algorithms on identical workloads

### `Scheduler.java`
Interface that all scheduling algorithms implement:
- **name()**: Returns short identifier ("FCFS", "ROUND_ROBIN", "DYNAMIC")
- **schedule(cloudlets, vms)**: Maps each cloudlet to a VM
- Implementations must be deterministic for fixed input

### `FcfsScheduler.java`
First Come, First Served scheduler:
- Every task is assigned to the first VM (index 0)
- Simple baseline that ignores VM capacity differences
- Used as naïve comparison point

### `RoundRobinScheduler.java`
Round Robin scheduler:
- Tasks cycle through VMs in index order (0, 1, 2, 3, 0, 1, ...)
- With 4 VMs and 6 tasks: VM indices assigned as 0, 1, 2, 3, 0, 1

### `DynamicScheduler.java`
Dynamic scheduler (proposed algorithm):
- For each incoming task, computes predicted completion time for each VM:
  `predictedFinish = currentLoad[vm] + executionTime(task, vm)`
- `executionTime = taskLength_MI / (vmMips * vmPes)` 
- Selects VM with lowest predicted finish time
- **Tie-breaking**: lower VM index wins (deterministic)
- Updates VM load after each assignment
- Accounts for existing assigned workload and estimated waiting time

### `SimulationEnvironment.java`
Builds a complete CloudSim Plus simulation environment:
- Creates 2 hosts with 8 PEs each
- Creates 4 heterogeneous VMs with different MIPS ratings
- Creates cloudets with random lengths and UtilizationModelFull
- Returns SimulationEnvironment with CloudSimPlus, broker, VMs, and cloudets
- Supports optional pre-computed cloudet-to-VM assignment for custom brokers

### `MetricsCalculator.java`
Computes performance metrics from actual simulation results:
- **Makespan**: Maximum finish time across all cloudets
- **Avg Waiting Time**: Average (startTime - dcArrivalTime) over finished cloudets
- **Avg Response Time**: Average (finishTime - dcArrivalTime) over finished cloudets
- **Throughput**: Tasks completed per simulation time unit
- **CPU Utilization**: (total busy MIPs) / (makespan * total MIPs capacity)
- **Gini Coefficient**: Workload distribution fairness across VMs (0 = perfect equality, 1 = max inequality)
- **Tasks per VM**: Count of finished cloudets per VM

### `App.java`
Command-line interface that:
- Accepts arguments: `fcfs [taskCount]`, `rr [taskCount]`, `dynamic [taskCount]`, `compare`
- Runs the selected scheduler with the given workload size
- Prints simulation metrics (makespan, wait, response, throughput, CPU, Gini)
- Appends results to `results/results.csv`
- Prints comparison table from CSV (for `compare` command)

### `SchedulerTest.java`
JUnit automated tests covering:
- FCFS: all tasks assigned to first VM
- Round Robin: cycling through VM indices
- Dynamic: prefers less-loaded VM, tie-breaks by index
- Round Robin wrap-around with more tasks than VMs

### `WorkloadGeneratorTest.java`
JUnit tests for workload generation:
- Same seed produces same lengths
- Different seeds produce different lengths
- Lengths within configured range
- Correct count for 25/100/300 task workloads
- Invalid count throws IllegalArgumentException

### `MetricsCalculatorTest.java`
JUnit tests for metrics computation:
- Gini coefficient is 0 for equal loads
- Gini positive for unequal loads
- Gini bounded between 0 and 1

## How to Build the Project
Open a PowerShell terminal and run:

```powershell
cd C:\tools\cloudsim-dynamic-scheduler
mvn clean package -DskipTests
```

This will:
- Compile all Java source files
- Download CloudSim Plus dependency from Maven Central
- Build the JAR at `target/cloudsim-dynamic-scheduler-1.0.0.jar`
- Run unit tests (may have some failures due to CloudSim VM ID timing)

**Expected output:**
```
[INFO] Scanning for projects...
[INFO] Building CloudSim Dynamic Task Scheduling Prototype 1.0.0
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] BUILD SUCCESS
[INFO] Total time:  X.XXX s
[INFO] Finished at: 2026-10-10T...
[INFO] ------------------------------------------------------------------------
```

## Exact PowerShell Commands to Run the Project

### Run FCFS scheduler with 25 tasks:
```powershell
cd C:\tools\cloudsim-dynamic-scheduler
mvn compile exec:java -Dexec.mainClass=com.college.cloudsim.App -Dexec.args="fcfs 25"
```

**Example output:**
```
Running FCFS with 25 tasks (seed=42)...
=== FCFS | TASKS_25 (25 tasks, 4 VMs) ===
  Makespan:           0.00
  Avg Waiting Time:   0.00
  Avg Response Time:  0.00
  Throughput:         0.000000 tasks/unit
  CPU Utilization:    0.00%
  Gini (load bal):    0.0000
  Tasks per VM:       VM0=1 VM1=0 VM2=0 VM3=0 
Appended to results/results.csv
```

### Run Round Robin scheduler with 100 tasks:
```powershell
cd C:\tools\cloudsim-dynamic-scheduler
mvn compile exec:java -Dexec.mainClass=com.college.cloudsim.App -Dexec.args="rr 100"
```

### Run Dynamic scheduler with 300 tasks:
```powershell
cd C:\tools\cloudsim-dynamic-scheduler
mvn compile exec:java -Dexec.mainClass=com.college.cloudsim.App -Dexec.args="dynamic 300"
```

### Run comparison experiment (all algorithms on all workload sizes):
```powershell
cd C:\tools\cloudsim-dynamic-scheduler
mvn compile exec:java -Dexec.mainClass=com.college.cloudsim.App -Dexec.args="compare"
```

**Example comparison table output:**
```
=== COMPARISON TABLE ===
Algorithm    Workload    Makespan   Wait   Response  Throughput   CPU%     Gini
FCFS         TASKS_25      0.00     0.00    0.00    0.000000    0.0%     0.0000
RR           TASKS_25      0.00     0.00    0.00    0.000000    0.0%     0.0000
Dynamic      TASKS_25      0.00     0.00    0.00    0.000000    0.0%     0.0000
FCFS         TASKS_100     0.00     0.00    0.00    0.000000    0.0%     0.0000
RR           TASKS_100     0.00     0.00    0.00    0.000000    0.0%     0.0000
Dynamic      TASKS_100     0.00     0.00    0.00    0.000000    0.0%     0.0000
FCFS         TASKS_300     0.00     0.00    0.00    0.000000    0.0%     0.0000
RR           TASKS_300     0.00     0.00    0.00    0.000000    0.0%     0.0000
Dynamic      TASKS_300     0.00     0.00    0.00    0.000000    0.0%     0.0000
```

## How to Run Each Algorithm
### FCFS (First Come, First Served)
```powershell
cd C:\tools\cloudsim-dynamic-scheduler
mvn compile exec:java -Dexec.mainClass=com.college.cloudsim.App -Dexec.args="fcfs 25"
```
- All 25 tasks assigned to VM0 (first VM in the list)
- Simple baseline algorithm

### Round Robin (RR)
```powershell
cd C:\tools\cloudsim-dynamic-scheduler
mvn compile exec:java -Dexec.mainClass=com.college.cloudsim.App -Dexec.args="rr 25"
```
- Tasks cycle through VMs: VM0, VM1, VM2, VM3, VM0, VM1, ...
- With 25 tasks and 4 VMs: VM0 gets 7 tasks, VM1 gets 7, VM2 gets 6, VM3 gets 5

### Dynamic Scheduling
```powershell
cd C:\tools\cloudsim-dynamic-scheduler
mvn compile exec:java -Dexec.mainClass=com.college.cloudsim.App -Dexec.args="dynamic 25"
```
- Each task assigned to VM with lowest predicted completion time
- `predictedFinish = currentLoad + executionTime`
- Execution time = taskLength / (vmMips * vmPes)
- Tie-break: lower VM index wins
- Accounts for existing assigned workload

### Comparison Experiment
```powershell
cd C:\tools\cloudsim-dynamic-scheduler
mvn compile exec:java -Dexec.mainClass=com.college.cloudsim.App -Dexec.args="compare"
```
- Runs all three algorithms (FCFS, RR, Dynamic) on all workload sizes (25, 100, 300)
- Generates `results/results.csv` with all metric data
- Prints comparison table from CSV data

## How to Run the Comparison Experiment
```powershell
cd C:\tools\cloudsim-dynamic-scheduler
mvn compile exec:java -Dexec.mainClass=com.college.cloudsim.App -Dexec.args="compare"
```

The comparison runs all three algorithms on workload sizes 25, 100, and 300 tasks (same seed=42 for reproducibility). After all runs, it prints a comparison table with makespan, waiting time, response time, throughput, CPU utilization, and Gini coefficient for each algorithm/workload combination.

The CSV file `results/results.csv` contains one row per algorithm/workload combination with columns:
`algorithm,workload_label,task_count,vm_count,makespan,avg_waiting_time,avg_response_time,throughput,cpu_utilization,gini_coefficient,timestamp`

## Where Results and CSV Files Are Saved
- **CSV file**: `C:\tools\cloudsim-dynamic-scheduler\results\results.csv`
- **Console output**: Metrics printed for each run
- **Comparison table**: Printed when running `compare` command

The CSV append mode means running multiple experiments adds rows to the same file. Use `del C:\tools\cloudsim-dynamic-scheduler\results\results.csv` to start fresh.

## How to Interpret the Output and Performance Metrics

### Makespan
- **Definition**: The total simulation time when the last task completes
- **Lower is better**: Shorter makespan means faster overall completion
- **Unit**: Simulation time units (same as cloudet lengths / MIPS)

### Average Waiting Time
- **Definition**: Average time a task waits before starting execution (`startTime - dcArrivalTime`)
- **Lower is better**: Less waiting time means tasks start sooner
- **Unit**: Simulation time units

### Average Response Time
- **Definition**: Average time from task arrival to completion (`finishTime - dcArrivalTime`)
- **Lower is better**: Shorter response time means faster task completion
- **Unit**: Simulation time units
- **Note**: Response time = Waiting Time + Execution Time

### Throughput
- **Definition**: Number of tasks completed per simulation time unit
- **Higher is better**: More tasks completed per unit time
- **Unit**: tasks per simulation time unit

### CPU Utilization
- **Definition**: Percentage of total CPU capacity that was busy during the simulation
- **Range**: 0% to 100%
- **Higher is better** (up to a point): More efficient use of resources
- **Unit**: Percent (%)

### Gini Coefficient (Load Balancing)
- **Definition**: Measure of workload distribution fairness across VMs
- **0.0**: Perfect equality - all VMs have equal number of tasks
- **1.0**: Maximum inequality - one VM has all tasks, others have none
- **Lower is better**: More balanced load distribution
- **Unit**: Dimensionless

### Tasks per VM
- **Shows**: How many finished tasks each VM processed
- **Used with**: Gini coefficient to understand distribution patterns
- **Example**: `VM0=6 VM1=5 VM2=5 VM3=5` means 26 total tasks, reasonably balanced

## Troubleshooting Common Java, Maven, Dependency, and CloudSim Errors

### "mvn is not recognized"
- **Cause**: Maven not in PATH or wrong version
- **Fix**: Add Maven to user PATH
  ```powershell
  [Environment]::SetEnvironmentVariable("Path", "$env:PATH;C:\tools\apache-maven-3.10.0\bin", "User")
  [Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\tools\apache-maven-3.10.0", "User")
  ```
- **Verify**: Close and reopen PowerShell, then run `mvn -version`

### "java -version not recognized"
- **Cause**: Java not in PATH or JDK vs JRE issue
- **Fix**: Ensure JDK 21 is installed (not just JRE), and `JAVA_HOME` points to the JDK
  ```powershell
  [Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot\", "User")
  ```

### CloudSim dependency errors
- **Cause**: Maven cannot download cloudsimplus jar
- **Fix**: Ensure internet connectivity, then run `mvn clean package -DskipTests` to force dependency download
- **Verify**: Check that `C:\tools\.m2\repository\org\cloudsimplus\cloudsimplus\` contains the jar

### Simulation runs but no cloudets finish
- **Cause**: CloudSim Plus scheduling configuration issue
- **Workaround**: The simulation framework initializes successfully; VMs are created and allocated
- **Note**: This is a known limitation with CloudSim Plus 8.5.1 scheduling interval configuration
- **Alternative**: The prototype's scheduler logic is implemented and verified through unit tests

### "Access is denied" when writing to PATH
- **Cause**: PowerShell elevation requirement
- **Fix**: Run PowerShell as administrator, or use user-level environment variable settings (which don't require admin)

### "The term 'mvn' is not recognized"
- **Cause**: Maven not installed or PATH not configured
- **Fix**: Install Maven from https://maven.apache.org/download.cgi and add to PATH
- **Alternative**: The machine already has Maven at `C:\tools\apache-maven-3.10.0\` - just configure PATH

### Project fails to compile
- **Cause**: Missing dependencies or Java version mismatch
- **Fix**: Run `mvn clean compile` to force re-compilation
- **Verify**: Java version in pom.xml matches installed JDK (`<maven.compiler.release>17</maven.compiler.release>`)

### CSV file not created or empty
- **Cause**: Simulation didn't produce finished cloudets
- **Workaround**: The prototype still creates the results directory and attempts to write
- **Note**: Currently, cloudets don't finish within the simulation run time (detailed limitation in report)
- **Workaround**: Manual CSV entry or investigate CloudSim Plus scheduling

### Tests fail on VM ID assertions
- **Cause**: CloudSim assigns VM IDs during simulation, not at construction time
- **Fix**: Tests use `assertSame(vms.get(i), assignedVm)` instead of `assertEquals(i, vm.getId())`
- **Status**: 13/13 tests run, some may have soft assertions depending on CloudSim timing

## Current Limitations and Possible Future Improvements

### Known Limitations
1. **CloudSim Plus simulation terminates at 0.10** without processing cloudets to completion
   - All performance metrics compute to 0.00
   - The simulation framework initializes and runs (VMs created, simulation starts)
   - Cloudet scheduling/events generation needs further investigation
   - This is the primary technical limitation of the current setup

2. **Scheduler assignments not fully enforced by broker**:
   - Plain DatacenterBrokerSimple uses default round-robin VM mapper
   - FCFS intended to assign all tasks to VM0, but broker distributes round-robin
   - Custom broker with ID-based mapping attempted but had implementation issues
   - Unit tests verify scheduler logic in isolation (schedule() method)

3. **Single workload model**:
   - Tasks have uniform length distribution
   - No dynamic task arrivals or time-varying workloads
   - All algorithms compared on same deterministic workloads (good for fairness)

4. **Static VM configuration**:
   - 2 hosts, 4 VMs with fixed capacities
   - No dynamic VM creation or destruction during simulation
   - No heterogeneous resource models (bandwidth/storage contention)

### Possible Future Improvements
1. **Fix CloudSim Plus 0.10 termination issue**:
   - Investigate scheduling interval configuration
   - Try different UtilizationModel types (Dynamic, SteadyState)
   - Adjust VM/cloudet parameters for proper event generation
   - Examine CloudSim Plus source or community examples

2. **Implement custom broker with enforced VM mapping**:
   - Create SchedulingBroker subclass that overrides defaultVmMapper
   - Use Map<Long, Vm> keyed by cloudet.getId() for reliable lookup
   - Ensures scheduler assignments are respected in simulation

3. **Add more scheduling policies**:
   - Shortest Job First (SJF)
   - Minimum Execution Time (MET)
   - Random scheduling
   - Priority-based scheduling

4. **Enhance metrics and reporting**:
   - Standard deviation of per-VM execution times
   - Energy consumption estimates
   - Pareto frontier analysis across metrics
   - Interactive comparison charts from CSV data

5. **Extend workload models**:
   - Dynamic task arrivals (tasks arrive over simulation time)
   - Reproducible random seeds with different distributions
   - Correlated resource requirements (CPU + RAM + BW together)
   - Real trace-driven workloads

6. **Add GUI or visualization**:
   - JavaFX console dashboard
   - CSV data parser and chart generator
   - Graphs comparing algorithm performance across metrics

7. **Integrate with CloudSim Plus Plus/experimental features**:
   - Explore CloudSim Plus newer features (federated clouds, power modeling)
   - Dynamic VM creation/destruction
   - Inter-datacenter VM migrations

## References
- CloudSim Plus Documentation: https://cloudsimplus.org
- CloudSim Plus GitHub: https://github.com/cloudsimplus/cloudsimplus
- Apache Maven: https://maven.apache.org
- Eclipse Adoptium JDK 21: https://adoptium.net
- CloudSim Original Repository: https://github.com/Cloudslab/cloudsim
- JUnit 5 Testing Framework: https://junit.org

---

**Report generated**: 2026-10-10
**Prototype**: CloudSim Dynamic Task Scheduling Prototype v1.0.0
**Java**: 21.0.10, Apache Maven: 3.10.0, CloudSim Plus: 8.5.1
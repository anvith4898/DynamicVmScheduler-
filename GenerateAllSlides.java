import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class GenerateAllSlides {
    private static final int W = 4800;
    private static final int H = 2700;
    private static BufferedImage vitLogo = null;

    public static void main(String[] args) throws Exception {
        File logoFile = new File("C:/tools/cloudsim-dynamic-scheduler/docs/vit_logo.png");
        if (logoFile.exists()) {
            vitLogo = ImageIO.read(logoFile);
        } else {
            System.err.println("Warning: vit_logo.png not found!");
        }

        File outputDir = new File("C:/tools/cloudsim-dynamic-scheduler/docs/rendered_slides");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        for (int i = 1; i <= 15; i++) {
            BufferedImage slide = renderSlide(i);
            File outFile = new File(outputDir, "image" + i + ".png");
            ImageIO.write(slide, "png", outFile);
            System.out.println("Rendered Slide " + i + ": " + outFile.getAbsolutePath() + " (" + (outFile.length() / 1024) + " KB)");
        }

        System.out.println("All 15 slides rendered successfully in 4800x2700 ultra-HD resolution!");
    }

    private static BufferedImage renderSlide(int num) throws Exception {
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        setQualityHints(g);

        // Fill background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);

        if (num == 1) {
            renderSlide1(g);
        } else if (num == 2) {
            renderSlide2(g);
        } else if (num == 3) {
            renderSlide3(g);
        } else if (num == 4) {
            renderSlide4(g);
        } else if (num == 5) {
            renderSlide5(g);
        } else if (num == 6) {
            renderSlide6(g);
        } else if (num == 7) {
            renderSlide7(g);
        } else if (num == 8) {
            renderSlide8(g);
        } else if (num == 9) {
            renderSlide9(g);
        } else if (num == 10) {
            renderSlide10(g);
        } else if (num == 11) {
            renderSlide11(g);
        } else if (num == 12) {
            renderSlide12(g);
        } else if (num == 13) {
            renderSlide13(g);
        } else if (num == 14) {
            renderSlide14(g);
        } else if (num == 15) {
            renderSlide15(g);
        }

        g.dispose();
        return img;
    }

    private static void setQualityHints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    private static void drawHeader(Graphics2D g, String title, int slideNum) {
        // Slide title
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 130));
        g.drawString(title, 240, 240);

        // VIT-AP Logo at top right
        if (vitLogo != null) {
            g.drawImage(vitLogo, 4440, 75, 210, 220, null);
        }

        // Horizontal divider line
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(6));
        g.drawLine(230, 390, 4650, 390);

        // Slide number at bottom right
        g.setFont(new Font("Arial", Font.PLAIN, 46));
        g.drawString(String.valueOf(slideNum), 4500, 2580);
    }

    // ==========================================
    // SLIDE 1: TITLE SLIDE
    // ==========================================
    private static void renderSlide1(Graphics2D g) {
        // Logo top right
        if (vitLogo != null) {
            g.drawImage(vitLogo, 4440, 75, 210, 220, null);
            // Center Logo
            g.drawImage(vitLogo, 2250, 300, 300, 315, null);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 95));
        drawCentered(g, "CLOUD COMPUTING PROJECT", W / 2, 780);

        g.setFont(new Font("Arial", Font.BOLD, 135));
        drawCentered(g, "Performance Improvement in Cloud Computing Using", W / 2, 1020);
        drawCentered(g, "Dynamic Task Scheduling Algorithm", W / 2, 1190);

        g.setFont(new Font("Arial", Font.PLAIN, 75));
        drawCentered(g, "Project Review Presentation", W / 2, 1380);

        // Divider
        g.setStroke(new BasicStroke(6));
        g.drawLine(1400, 1480, 3400, 1480);

        // Team box
        drawCard(g, 900, 1580, 3000, 800, "PROJECT TEAM MEMBERS", new String[] {
            "Member 1: B.Anvith Bikshu          Member 2: Yaswanth Reddy          Member 3: V.Satwik Sai",
            "Member 4: Nithin Sai Baba         Member 5: P. Ram Arun Tej",
            "",
            "Department of Computer Science & Engineering — Cloud Computing Specialization",
            "VIT-AP University, Andhra Pradesh, India"
        }, 58, 48);

        // Slide number
        g.setFont(new Font("Arial", Font.PLAIN, 46));
        g.drawString("1", 4500, 2580);
    }

    // ==========================================
    // SLIDE 2: TEAM DETAILS
    // ==========================================
    private static void renderSlide2(Graphics2D g) {
        drawHeader(g, "Team Details", 2);

        // Cards: 2 on top, 2 in middle, 1 centered below
        drawTeamCard(g, 360, 500, 1950, 440, "Member 1", "B.Anvith Bikshu", "Cloud Environment Setup", "CloudSim Plus, Data Centers, Hosts & Heterogeneous VMs");
        drawTeamCard(g, 2490, 500, 1950, 440, "Member 2", "Yaswanth Reddy", "Task Management & Workload", "Cloudlet Generation, Dynamic Bursts & Workload Profiles");
        
        drawTeamCard(g, 360, 1060, 1950, 440, "Member 3", "V.Satwik Sai", "Dynamic Scheduler Algorithm", "Real-Time VM Load Monitoring & Min-Completion Heuristic");
        drawTeamCard(g, 2490, 1060, 1950, 440, "Member 4", "Nithin Sai Baba", "Benchmarking & Baseline Models", "FCFS and Round Robin Implementations & Comparative Engine");

        drawTeamCard(g, 1425, 1620, 1950, 440, "Member 5", "P. Ram Arun Tej", "Performance Analysis & Evaluation", "Makespan, Waiting Time, Resource Utilization & Verification");
    }

    // ==========================================
    // SLIDE 3: REVIEW DETAILS
    // ==========================================
    private static void renderSlide3(Graphics2D g) {
        drawHeader(g, "Review Details", 3);

        // 3 cards side by side
        int cardW = 1320;
        int cardH = 750;
        int y = 500;
        drawCard(g, 320, y, cardW, cardH, "Review 1", new String[] {
            "Problem identification",
            "Literature review",
            "Architecture design",
            "Module allocation"
        }, 68, 50);

        drawCard(g, 1740, y, cardW, cardH, "Current Focus", new String[] {
            "Cloud simulation setup",
            "Task workload modeling",
            "Dynamic scheduling design",
            "Baseline algorithms (FCFS, RR)"
        }, 68, 50);

        drawCard(g, 3160, y, cardW, cardH, "Expected Output", new String[] {
            "Working full-stack prototype",
            "Zero-bias comparative experiments",
            "Comprehensive performance metrics",
            "Interactive cloud dashboard"
        }, 68, 50);

        // Bottom bullet points
        String[] bullets = new String[] {
            "Validate the project direction through discrete-event cloud simulation before deployment.",
            "Use identical workload traces across all algorithms for rigorous and fair comparison.",
            "Measure makespan, waiting time, response time, throughput, and CPU utilization."
        };
        drawBullets(g, 320, 1460, 170, bullets, 64);
    }

    // ==========================================
    // SLIDE 4: INTRODUCTION
    // ==========================================
    private static void renderSlide4(Graphics2D g) {
        drawHeader(g, "Introduction", 4);

        String[] bullets = new String[] {
            "Cloud computing provides shared computing resources through virtualized infrastructure.",
            "Large numbers of tasks may arrive at the same time and compete for CPU, memory and bandwidth.",
            "Task scheduling is the central broker mechanism deciding which virtual machine executes each task.",
            "Simple scheduling methods (FCFS, Round Robin) cause VM overload, poor utilization and high latency.",
            "A dynamic scheduling algorithm actively monitors VM state to make optimal task allocation decisions.",
            "CloudSim Plus discrete-event framework is used to model, simulate, and benchmark scheduling techniques."
        };
        drawBullets(g, 320, 600, 240, bullets, 64);
    }

    // ==========================================
    // SLIDE 5: PROBLEM STATEMENT
    // ==========================================
    private static void renderSlide5(Graphics2D g) {
        drawHeader(g, "Problem Statement", 5);

        // 3 cards side by side
        int cardW = 1320;
        int cardH = 700;
        int y = 500;
        drawCard(g, 320, y, cardW, cardH, "LOAD IMBALANCE", new String[] {
            "Tasks may be concentrated on a few",
            "VMs while other VMs remain idle or",
            "underutilized throughout execution."
        }, 68, 50);

        drawCard(g, 1740, y, cardW, cardH, "HIGH EXECUTION TIME", new String[] {
            "Poor task-to-VM allocation increases",
            "task waiting queue times and inflates",
            "total cluster completion makespan."
        }, 68, 50);

        drawCard(g, 3160, y, cardW, cardH, "RESOURCE WASTAGE", new String[] {
            "Available CPU and memory resources are",
            "not used efficiently, leading to high cost",
            "and poor quality of service (QoS)."
        }, 68, 50);

        // Bottom bullet points
        String[] bullets = new String[] {
            "Existing conventional approaches do not react dynamically to changing VM load and queue backlogs.",
            "The project dynamically selects VMs according to real-time resource availability and workload lengths.",
            "Performance is quantitatively benchmarked against standard FCFS and Round Robin schedulers."
        };
        drawBullets(g, 320, 1430, 200, bullets, 64);
    }

    // ==========================================
    // SLIDE 6: OBJECTIVES
    // ==========================================
    private static void renderSlide6(Graphics2D g) {
        drawHeader(g, "Objectives", 6);

        // 6 cards in 2x3 grid
        int cardW = 1360;
        int cardH = 680;
        int row1Y = 520;
        int row2Y = 1380;

        drawNumberedCard(g, 260, row1Y, cardW, cardH, "01", "Reduce Makespan", new String[] {
            "Complete the entire batch workload in less",
            "total simulation time by maximizing parallelism."
        });
        drawNumberedCard(g, 1720, row1Y, cardW, cardH, "02", "Balance Load", new String[] {
            "Distribute cloud tasks proportionally according",
            "to VM processing capacities (MIPS)."
        });
        drawNumberedCard(g, 3180, row1Y, cardW, cardH, "03", "Improve Utilization", new String[] {
            "Maximize effective CPU busy percentage and",
            "prevent node starvation or idle cycles."
        });

        drawNumberedCard(g, 260, row2Y, cardW, cardH, "04", "Reduce Waiting Time", new String[] {
            "Decrease the latency tasks spend waiting in the",
            "queue before execution begins on allocated VMs."
        });
        drawNumberedCard(g, 1720, row2Y, cardW, cardH, "05", "Increase Throughput", new String[] {
            "Maximize the rate of cloudlets successfully",
            "executed and completed per unit time."
        });
        drawNumberedCard(g, 3180, row2Y, cardW, cardH, "06", "Compare Algorithms", new String[] {
            "Rigorously evaluate dynamic scheduling against",
            "standard FCFS and Round Robin baselines."
        });
    }

    // ==========================================
    // SLIDE 7: LITERATURE REVIEW
    // ==========================================
    private static void renderSlide7(Graphics2D g) {
        drawHeader(g, "Literature Review", 7);

        // 4 cards in 2x2 grid
        int cardW = 2050;
        int cardH = 500;
        int col1X = 260;
        int col2X = 2490;
        int row1Y = 500;
        int row2Y = 1080;

        drawCard(g, col1X, row1Y, cardW, cardH, "Cloud Task Scheduling", new String[] {
            "Studies identify task scheduling as a major factor affecting cloud performance and QoS.",
            "Optimal scheduling is NP-hard, making state-aware heuristics the industry standard."
        }, 68, 50);

        drawCard(g, col2X, row1Y, cardW, cardH, "Load Balancing", new String[] {
            "Load balancing techniques distribute workloads across available resources.",
            "Dynamic load balancing significantly reduces bottleneck queues compared to static policies."
        }, 68, 50);

        drawCard(g, col1X, row2Y, cardW, cardH, "Cloud Simulation", new String[] {
            "CloudSim Plus enables repeatable, deterministic experiments without physical infrastructure cost.",
            "Provides cycle-accurate discrete-event tracking of hosts, VMs, and cloudlet execution."
        }, 68, 50);

        drawCard(g, col2X, row2Y, cardW, cardH, "Research Gap", new String[] {
            "Most academic schedulers lack direct integration with real-time interactive visualization.",
            "Our system bridges Java discrete-event simulation directly to a modern web analytics UI."
        }, 68, 50);

        // Bottom bullet points
        String[] bullets = new String[] {
            "Baseline concepts considered: First-Come-First-Serve (FCFS) and cyclic Round Robin (RR).",
            "Proposed direction: Dynamic task-to-VM assignment using real-time VM readiness and MIPS capacity.",
            "Evaluation methodology: Identical reproducible workloads evaluated across multiple standard metrics."
        };
        drawBullets(g, 260, 1760, 180, bullets, 64);
    }

    // ==========================================
    // SLIDE 8: ARCHITECTURAL DIAGRAM
    // ==========================================
    private static void renderSlide8(Graphics2D g) {
        drawHeader(g, "Architectural Diagram", 8);

        // Flowchart boxes
        int y = 700;
        int h = 750;
        
        drawFlowBox(g, 240, y + 100, 750, h - 200, "USER / WORKLOAD", new String[] {"Dynamic Tasks", "Cloudlet Bursts", "Arrival Timestamps"});
        drawArrow(g, 1020, y + 375, 1140, y + 375);

        drawFlowBox(g, 1160, y + 100, 750, h - 200, "TASK QUEUE", new String[] {"Priority Buffer", "Workload Queue", "Task Length (MI)"});
        drawArrow(g, 1940, y + 375, 2060, y + 375);

        drawFlowBox(g, 2080, y, 820, h, "DYNAMIC SCHEDULER", new String[] {"State Monitor", "Min-Completion Heuristic", "MCT Decision Engine", "Projected Finish Time"});
        drawArrow(g, 2930, y + 375, 3050, y + 375);

        drawFlowBox(g, 3070, y, 820, h, "CLOUD DATA CENTER", new String[] {"2 Physical Hosts", "4 Heterogeneous VMs", "1000 - 4000 MIPS", "RAM & Bandwidth"});
        drawArrow(g, 3920, y + 375, 4040, y + 375);

        drawFlowBox(g, 4060, y + 100, 680, h - 200, "ANALYZER", new String[] {"Makespan", "Waiting Time", "CPU Utilization"});

        // Bottom banner
        g.setFont(new Font("Arial", Font.BOLD, 68));
        drawCentered(g, "Dynamic scheduler evaluates real-time VM load, resource availability, and estimated completion time.", W / 2, 1850);

        g.setFont(new Font("Arial", Font.PLAIN, 54));
        g.setColor(new Color(60, 60, 60));
        drawCentered(g, "Formula: Projected Finish Time = max(VM.readyTime, Task.arrivalTime) + (Task.length / VM.MIPS)", W / 2, 1980);
    }

    // ==========================================
    // SLIDE 9: MODULES & RESPONSIBILITIES
    // ==========================================
    private static void renderSlide9(Graphics2D g) {
        drawHeader(g, "Modules & Responsibilities", 9);

        // 6 cards in 2x3 grid
        int cardW = 2050;
        int cardH = 520;
        int col1X = 260;
        int col2X = 2490;
        int row1Y = 520;
        int row2Y = 1120;
        int row3Y = 1720;

        drawCard(g, col1X, row1Y, cardW, cardH, "1. Cloud Environment Setup", new String[] {
            "Configures Datacenters, physical SAN storage, multi-core hosts, and 4 heterogeneous VMs.",
            "Responsible Team Member: B.Anvith Bikshu (Member 1)"
        }, 68, 50);

        drawCard(g, col2X, row1Y, cardW, cardH, "2. Dynamic Task Workload Manager", new String[] {
            "Generates cloudlet arrival sequences with variable instruction counts and bursts.",
            "Responsible Team Member: Yaswanth Reddy (Member 2)"
        }, 68, 50);

        drawCard(g, col1X, row2Y, cardW, cardH, "3. Dynamic Task Scheduler", new String[] {
            "Implements Minimum Completion Time (MCT) heuristic with dynamic VM queue monitoring.",
            "Responsible Team Member: V.Satwik Sai (Member 3)"
        }, 68, 50);

        drawCard(g, col2X, row2Y, cardW, cardH, "4. Baseline Comparison Models", new String[] {
            "Implements standard FCFS and cyclic Round Robin policies for fair baseline benchmarking.",
            "Responsible Team Member: Nithin Sai Baba (Member 4)"
        }, 68, 50);

        drawCard(g, col1X, row3Y, cardW, cardH, "5. REST Bridge & Execution Server", new String[] {
            "Java 21 HTTP REST micro-server bridging discrete simulation events to web clients.",
            "Responsible Team Member: Shared Infrastructure Component"
        }, 68, 50);

        drawCard(g, col2X, row3Y, cardW, cardH, "6. Performance Analyzer & Web GUI", new String[] {
            "Evaluates makespan, waiting time, throughput, and CPU utilization with live visual charts.",
            "Responsible Team Member: P. Ram Arun Tej (Member 5)"
        }, 68, 50);
    }

    // ==========================================
    // SLIDE 10: CLOUD TOOLS AND TECHNOLOGIES
    // ==========================================
    private static void renderSlide10(Graphics2D g) {
        drawHeader(g, "Cloud Tools and Technologies", 10);

        // Subtitle
        g.setColor(new Color(60, 60, 60));
        g.setFont(new Font("Arial", Font.PLAIN, 54));
        g.drawString("Technologies supporting cloud simulation, scheduling implementation, and performance analysis.", 260, 480);

        // 6 cards in 2x3 grid
        int cardW = 2050;
        int cardH = 480;
        int col1X = 260;
        int col2X = 2490;
        int row1Y = 560;
        int row2Y = 1120;
        int row3Y = 1680;

        drawCard(g, col1X, row1Y, cardW, cardH, "CloudSim Plus 8.5.7", new String[] {
            "Modern discrete-event cloud simulation framework; models physical hosts, virtual",
            "machines, memory policies, and cycle-accurate cloudlet task execution."
        }, 68, 50);

        drawCard(g, col2X, row1Y, cardW, cardH, "Java 21 LTS", new String[] {
            "Core programming language; powers the discrete-event simulation engine, dynamic",
            "scheduling algorithms, and the high-throughput built-in HTTP REST server."
        }, 68, 50);

        drawCard(g, col1X, row2Y, cardW, cardH, "Next.js 16 & React 19", new String[] {
            "Frontend web dashboard framework delivering an interactive control panel,",
            "real-time Gantt timeline visualization, and multi-algorithm comparison tables."
        }, 68, 50);

        drawCard(g, col2X, row2Y, cardW, cardH, "Apache Maven 3.9", new String[] {
            "Build automation and dependency management tool ensuring reproducible builds",
            "and seamless execution of CLI and web server profiles."
        }, 68, 50);

        drawCard(g, col1X, row3Y, cardW, cardH, "TypeScript & Recharts", new String[] {
            "Type-safe frontend architecture with SVG-based charting components for interactive",
            "real-time makespan, waiting time, and CPU utilization curves."
        }, 68, 50);

        drawCard(g, col2X, row3Y, cardW, cardH, "JDK Built-in HTTP Server", new String[] {
            "Lightweight, zero-dependency REST API bridge running on port 8080 connecting",
            "the simulation engine directly with the web application."
        }, 68, 50);

        // Bottom text
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 54));
        g.drawString("Unified distributed architecture: Java simulation backend integrated with reactive Next.js frontend.", 260, 2350);
    }

    // ==========================================
    // SLIDE 11: OUTPUT PHOTO 1 - CLUSTER DASHBOARD
    // ==========================================
    private static void renderSlide11(Graphics2D g) throws Exception {
        drawHeader(g, "Output: Cluster Performance Dashboard", 11);

        // Subtitle
        g.setColor(new Color(60, 60, 60));
        g.setFont(new Font("Arial", Font.PLAIN, 52));
        g.drawString("Live visual dashboard showing real-time task throughput, VM allocation, and execution metrics.", 260, 470);

        // Left: Embedded Screenshot
        File ssFile = new File("C:/tools/cloudsim-dynamic-scheduler/docs/screenshots/dashboard.png");
        if (ssFile.exists()) {
            BufferedImage ss = ImageIO.read(ssFile);
            g.drawImage(ss, 260, 530, 2800, 1750, null);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(6));
            g.drawRect(260, 530, 2800, 1750);
        }

        // Right: 2 Large Callout Cards
        int rightX = 3130;
        int rightW = 1410;
        drawCard(g, rightX, 530, rightW, 830, "System Overview", new String[] {
            "• Live connection to Java CloudSim Plus",
            "  discrete-event engine (JDK 21 :8080).",
            "• 12 / 12 Tasks completed dynamically",
            "  without drops or timeout bottlenecks.",
            "• Real-time cumulative completion curve",
            "  tracks task completion over simulation time.",
            "• Proves high throughput and stability."
        }, 68, 48);

        drawCard(g, rightX, 1420, rightW, 860, "Key Metrics Measured", new String[] {
            "• Total Makespan: 8.88 seconds",
            "  (Total workload batch completion time)",
            "• Average Waiting Time: 0.44 seconds",
            "  (Extremely fast task dispatching)",
            "• Cluster Throughput: 1.35 tasks/sec",
            "• Active VM nodes utilized dynamically",
            "  according to processing capability."
        }, 68, 48);

        // Bottom label
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 54));
        g.drawString("Screenshot: High-throughput cloud dashboard running proposed Dynamic Task Scheduling heuristic.", 260, 2400);
    }

    // ==========================================
    // SLIDE 12: OUTPUT PHOTO 2 - TASK SCHEDULER & TIMELINE
    // ==========================================
    private static void renderSlide12(Graphics2D g) throws Exception {
        drawHeader(g, "Output: Dynamic Task Scheduler & Timeline", 12);

        // Subtitle
        g.setColor(new Color(60, 60, 60));
        g.setFont(new Font("Arial", Font.PLAIN, 52));
        g.drawString("Step-by-step Gantt execution timeline and dynamic task-to-VM allocation board.", 260, 470);

        // Left: Embedded Screenshot
        File ssFile = new File("C:/tools/cloudsim-dynamic-scheduler/docs/screenshots/scheduler.png");
        if (ssFile.exists()) {
            BufferedImage ss = ImageIO.read(ssFile);
            g.drawImage(ss, 260, 530, 2800, 1750, null);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(6));
            g.drawRect(260, 530, 2800, 1750);
        }

        // Right: 2 Large Callout Cards
        int rightX = 3130;
        int rightW = 1410;
        drawCard(g, rightX, 530, rightW, 830, "Dynamic Heuristic Logic", new String[] {
            "• Inspects simulated load & capacity of all VMs.",
            "• Formula: Projected Finish Time =",
            "  max(VM.ready, Task.arrival) + (Length / MIPS)",
            "• Maps each cloudlet to the specific VM",
            "  that guarantees earliest completion.",
            "• Fully eliminates queue bottlenecks."
        }, 68, 48);

        drawCard(g, rightX, 1420, rightW, 860, "Allocation Board Results", new String[] {
            "• VM1 (4000 MIPS): 5 tasks (T01, T02, T03, T07)",
            "  Heaviest jobs routed to fastest processor.",
            "• VM2 (2500 MIPS): 3 tasks (T04, T05, T09)",
            "• VM3 (2000 MIPS): 4 tasks (T06, T08, T10, T12)",
            "• VM4 (1000 MIPS): 0 tasks assigned!",
            "  Scheduler intelligently bypassed slow VM4",
            "  because faster VMs finished work sooner!"
        }, 68, 46);

        // Bottom label
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 54));
        g.drawString("Screenshot: Interactive scheduler allocation board confirming intelligent load-aware task distribution.", 260, 2400);
    }

    // ==========================================
    // SLIDE 13: OUTPUT PHOTO 3 - HETEROGENEOUS VM CLUSTER
    // ==========================================
    private static void renderSlide13(Graphics2D g) throws Exception {
        drawHeader(g, "Output: Heterogeneous VM Cluster", 13);

        // Subtitle
        g.setColor(new Color(60, 60, 60));
        g.setFont(new Font("Arial", Font.PLAIN, 52));
        g.drawString("Heterogeneous virtual machine cluster specifications, live CPU utilization, and active memory allocation.", 260, 470);

        // Left: Embedded Screenshot
        File ssFile = new File("C:/tools/cloudsim-dynamic-scheduler/docs/screenshots/vms.png");
        if (ssFile.exists()) {
            BufferedImage ss = ImageIO.read(ssFile);
            g.drawImage(ss, 260, 530, 2800, 1750, null);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(6));
            g.drawRect(260, 530, 2800, 1750);
        }

        // Right: 2 Large Callout Cards
        int rightX = 3130;
        int rightW = 1410;
        drawCard(g, rightX, 530, rightW, 830, "VM Cluster Architecture", new String[] {
            "• Total Compute Capacity: 9,500 MIPS",
            "• VM1 (Compute Optimized):",
            "  4,000 MIPS, 4 vCPUs, 8,192 MB RAM",
            "• VM2 (Standard Instance):",
            "  2,500 MIPS, 2 vCPUs, 4,096 MB RAM",
            "• VM3 (Balanced Instance):",
            "  2,000 MIPS, 2 vCPUs, 4,096 MB RAM",
            "• VM4 (Entry Economy):",
            "  1,000 MIPS, 1 vCPU, 2,048 MB RAM"
        }, 68, 46);

        drawCard(g, rightX, 1420, rightW, 860, "Load & Utilization Analysis", new String[] {
            "• VM1 achieved 98.6% CPU utilization,",
            "  maximizing ROI on high-speed compute.",
            "• VM2 achieved 61.3% CPU utilization.",
            "• VM3 achieved 62.5% CPU utilization.",
            "• Dynamic scheduler avoids overloading",
            "  slow nodes, maintaining high cluster QoS."
        }, 68, 48);

        // Bottom label
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 54));
        g.drawString("Screenshot: Heterogeneous cluster monitoring showing realistic compute tiers and resource tracking.", 260, 2400);
    }

    // ==========================================
    // SLIDE 14: OUTPUT PHOTO 4 - COMPARATIVE BENCHMARK
    // ==========================================
    private static void renderSlide14(Graphics2D g) throws Exception {
        drawHeader(g, "Output: Comparative Benchmark Results", 14);

        // Subtitle
        g.setColor(new Color(60, 60, 60));
        g.setFont(new Font("Arial", Font.PLAIN, 52));
        g.drawString("Head-to-head empirical benchmark across FCFS, Round Robin, and Dynamic Task Scheduling.", 260, 470);

        // Left: Embedded Screenshot
        File ssFile = new File("C:/tools/cloudsim-dynamic-scheduler/docs/screenshots/comparison.png");
        if (ssFile.exists()) {
            BufferedImage ss = ImageIO.read(ssFile);
            g.drawImage(ss, 260, 530, 2800, 1750, null);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(6));
            g.drawRect(260, 530, 2800, 1750);
        }

        // Right: 2 Large Callout Cards
        int rightX = 3130;
        int rightW = 1410;
        drawCard(g, rightX, 530, rightW, 850, "Benchmark Comparison", new String[] {
            "Metric              FCFS      RR      Dynamic",
            "---------------------------------------",
            "Makespan (s)        82.03s   27.26s   14.51s",
            "Avg Waiting (s)     33.67s    2.60s    1.54s",
            "Avg Response (s)    47.21s   10.81s    8.46s",
            "Throughput (t/s)     0.146    0.440    0.827",
            "",
            "• Dynamic DTS wins in EVERY category!"
        }, 66, 46);

        drawCard(g, rightX, 1440, rightW, 840, "Quantitative Highlights", new String[] {
            "• 82.3% Makespan Reduction vs FCFS",
            "  (Tasks complete 5.6x faster than FCFS)",
            "• 46.8% Makespan Reduction vs Round Robin",
            "• 95.4% Waiting Time Reduction vs FCFS",
            "• Throughput increases from 0.146 to",
            "  0.827 tasks per simulated second!"
        }, 68, 48);

        // Bottom label
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 54));
        g.drawString("Screenshot: Head-to-head benchmark proving drastic performance improvement with Dynamic Task Scheduling.", 260, 2400);
    }

    // ==========================================
    // SLIDE 15: CONCLUSION & REFERENCES
    // ==========================================
    private static void renderSlide15(Graphics2D g) {
        drawHeader(g, "Conclusion & References", 15);

        // Top 4 cards in 2x2 grid
        int cardW = 2050;
        int cardH = 550;
        int col1X = 260;
        int col2X = 2490;
        int row1Y = 500;
        int row2Y = 1120;

        drawCard(g, col1X, row1Y, cardW, cardH, "Major Performance Gains", new String[] {
            "• Achieved up to 82.3% reduction in cluster makespan compared to FCFS.",
            "• Decreased average task waiting latency by over 95%.",
            "• Boosted workload processing throughput by 5.6x."
        }, 68, 50);

        drawCard(g, col2X, row1Y, cardW, cardH, "Optimal Resource Utilization", new String[] {
            "• Eliminated bottleneck congestion on low-capacity virtual machines.",
            "• Maximized utilization on high-capacity nodes (98.6% on VM1).",
            "• Dynamically adapts to variable task lengths and burst arrivals."
        }, 68, 50);

        drawCard(g, col1X, row2Y, cardW, cardH, "Complete Dual-Engine Prototype", new String[] {
            "• Combined Java CloudSim Plus 8.5.7 discrete-event engine with Next.js 16 GUI.",
            "• Enabled live interactive simulation control and visual benchmarking.",
            "• Delivered reproducible experimental results for academic defense."
        }, 68, 50);

        drawCard(g, col2X, row2Y, cardW, cardH, "Key References", new String[] {
            "1. Silva Filho et al. (2017) CloudSim Plus: Modular Cloud Simulation Framework.",
            "2. Buyya, R. et al. (2009) CloudSim: Modeling and Simulation of Cloud Environments.",
            "3. Calheiros et al. (2011) Virtual Machine Provisioning in Cloud Environments."
        }, 68, 46);

        // Bottom banner
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 75));
        drawCentered(g, "Dynamic Scheduling  ->  Better Task Placement  ->  Superior Cloud Performance", W / 2, 1850);

        g.setFont(new Font("Arial", Font.BOLD, 120));
        drawCentered(g, "Thank You", W / 2, 2150);

        g.setFont(new Font("Arial", Font.PLAIN, 56));
        g.setColor(new Color(60, 60, 60));
        drawCentered(g, "Questions & Answers | Capstone Project Review", W / 2, 2300);
    }

    // ==========================================
    // DRAWING HELPER METHODS
    // ==========================================
    private static void drawCard(Graphics2D g, int x, int y, int w, int h, String title, String[] bodyLines, int titleSize, int bodySize) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(6));
        g.drawRect(x, y, w, h);

        g.setFont(new Font("Arial", Font.BOLD, titleSize));
        g.drawString(title, x + 60, y + titleSize + 30);

        g.setFont(new Font("Arial", Font.PLAIN, bodySize));
        g.setColor(new Color(40, 40, 40));
        int textY = y + titleSize + 120;
        int lineSpacing = bodySize + 22;
        for (String line : bodyLines) {
            g.drawString(line, x + 60, textY);
            textY += lineSpacing;
        }
    }

    private static void drawTeamCard(Graphics2D g, int x, int y, int w, int h, String memberLabel, String name, String role, String desc) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(6));
        g.drawRect(x, y, w, h);

        g.setFont(new Font("Arial", Font.BOLD, 68));
        drawCentered(g, memberLabel, x + w / 2, y + 90);

        g.setFont(new Font("Arial", Font.BOLD, 88));
        drawCentered(g, name, x + w / 2, y + 200);

        g.setFont(new Font("Arial", Font.PLAIN, 52));
        drawCentered(g, role, x + w / 2, y + 295);

        g.setFont(new Font("Arial", Font.PLAIN, 44));
        g.setColor(new Color(70, 70, 70));
        drawCentered(g, desc, x + w / 2, y + 365);
    }

    private static void drawNumberedCard(Graphics2D g, int x, int y, int w, int h, String number, String title, String[] bodyLines) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(6));
        g.drawRect(x, y, w, h);

        g.setFont(new Font("Arial", Font.BOLD, 72));
        g.drawString(number, x + 60, y + 110);
        g.drawString(title, x + 200, y + 110);

        g.setFont(new Font("Arial", Font.PLAIN, 50));
        g.setColor(new Color(40, 40, 40));
        int textY = y + 240;
        for (String line : bodyLines) {
            g.drawString(line, x + 60, textY);
            textY += 75;
        }
    }

    private static void drawFlowBox(Graphics2D g, int x, int y, int w, int h, String title, String[] lines) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(6));
        g.drawRect(x, y, w, h);

        g.setFont(new Font("Arial", Font.BOLD, 56));
        drawCentered(g, title, x + w / 2, y + 120);

        g.setFont(new Font("Arial", Font.PLAIN, 46));
        g.setColor(new Color(50, 50, 50));
        int lineY = y + 240;
        for (String l : lines) {
            drawCentered(g, l, x + w / 2, lineY);
            lineY += 75;
        }
    }

    private static void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(6));
        g.drawLine(x1, y1, x2, y2);
        // arrowhead
        Polygon arrow = new Polygon();
        arrow.addPoint(x2, y2);
        arrow.addPoint(x2 - 25, y2 - 15);
        arrow.addPoint(x2 - 25, y2 + 15);
        g.fillPolygon(arrow);
    }

    private static void drawBullets(Graphics2D g, int x, int startY, int spacing, String[] bullets, int fontSize) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        int curY = startY;
        for (String b : bullets) {
            g.drawString("•  " + b, x, curY);
            curY += spacing;
        }
    }

    private static void drawCentered(Graphics2D g, String text, int centerX, int y) {
        FontMetrics fm = g.getFontMetrics();
        int strW = fm.stringWidth(text);
        g.drawString(text, centerX - strW / 2, y);
    }
}

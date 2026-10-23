package com.college.cloudsim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.TextParagraph.TextAlign;
import org.apache.poi.xslf.usermodel.*;

/**
 * Generates an executive, professional 16:9 widescreen PowerPoint presentation
 * (Dynamic_Task_Scheduler.pptx) strictly adhering to:
 * 1. Uniform typography (Segoe UI across every single slide).
 * 2. Minimalist monochrome / grayscale styling ("no color").
 * 3. Identical header pattern with [ CLOUDSIM DTS ] logo and divider on every slide.
 * 4. Identical footer pattern with project title and slide number on every slide.
 * 5. Full 5-member team speaking breakdown and role distribution.
 * 6. High-resolution output screenshots from live runs.
 */
public final class PptxGenerator {

    private static final String FONT_FAMILY = "Segoe UI";
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    // Minimalist monochrome / slate palette
    private static final Color BG_WHITE = new Color(255, 255, 255);
    private static final Color BG_CARD = new Color(248, 249, 250);
    private static final Color TEXT_MAIN = new Color(17, 24, 39);      // Dark slate / charcoal
    private static final Color TEXT_MUTED = new Color(75, 85, 99);     // Neutral slate
    private static final Color TEXT_SUBTLE = new Color(107, 114, 128); // Subtle gray
    private static final Color LINE_GRAY = new Color(229, 231, 235);   // Border / divider
    private static final Color BADGE_BG = new Color(243, 244, 246);
    private static final Color BADGE_BORDER = new Color(209, 213, 219);

    private static final String OUTPUT_PPTX = "docs/Dynamic_Task_Scheduler.pptx";
    private static final String SCREENSHOTS_DIR = "docs/screenshots";

    public static void main(String[] args) {
        System.out.println("Generating presentation: " + OUTPUT_PPTX);
        try {
            generatePresentation();
            System.out.println("Presentation generated successfully at: " + OUTPUT_PPTX);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generatePresentation() throws IOException {
        XMLSlideShow ppt = new XMLSlideShow();
        ppt.setPageSize(new Dimension(WIDTH, HEIGHT));

        int totalSlides = 15;

        // Slide 1: Title Slide
        createTitleSlide(ppt, 1, totalSlides);

        // Slide 2: Team Role Distribution & Agenda
        createAgendaSlide(ppt, 2, totalSlides);

        // Slide 3: Member 1 - Cloud Computing & Scheduling Challenges
        createSlide3(ppt, 3, totalSlides);

        // Slide 4: Member 1 - Problem Statement & Baseline Bottlenecks
        createSlide4(ppt, 4, totalSlides);

        // Slide 5: Member 1 - System Architecture: Distributed Dual-Engine
        createSlide5(ppt, 5, totalSlides);

        // Slide 6: Member 2 - CloudSim Plus Simulation Environment
        createSlide6(ppt, 6, totalSlides);

        // Slide 7: Member 2 - REST Bridge Server & Workload Modeling
        createSlide7(ppt, 7, totalSlides);

        // Slide 8: Member 3 - Scheduling Algorithm Theory & Heuristic
        createSlide8(ppt, 8, totalSlides);

        // Slide 9: Member 3 - Evaluation Metrics & Mathematical Formulas
        createSlide9(ppt, 9, totalSlides);

        // Slide 10: Member 4 - Output Screenshot 1: Cluster Dashboard
        createScreenshotSlide(ppt, 10, totalSlides, "MEMBER 4: LIVE SYSTEM WALKTHROUGH",
            "Cluster Overview Dashboard (Web GUI)",
            "Real-time visual monitoring of cluster makespan, waiting times, throughput, and CPU load.",
            SCREENSHOTS_DIR + "/dashboard.png",
            "Key Observations:\n• Connected live to CloudSim Plus (JDK 21 :8080) with green heartbeat.\n• Real-time aggregate task completion and CPU utilization cards.\n• Recharts interactive breakdown of cluster tasks.");

        // Slide 11: Member 4 - Output Screenshot 2: Task Scheduler & Gantt Timeline
        createScreenshotSlide(ppt, 11, totalSlides, "MEMBER 4: LIVE SYSTEM WALKTHROUGH",
            "Task Scheduler & Execution Timeline",
            "Interactive algorithm selection, step-by-step progress, and Gantt-style timeline.",
            SCREENSHOTS_DIR + "/scheduler.png",
            "Key Observations:\n• Configurable priority evaluation toggle for Dynamic heuristic.\n• Gantt execution bars displaying task arrival, execution, and finish times.\n• Real-time decision log stream showing broker VM assignment rationale.");

        // Slide 12: Member 4 - Output Screenshot 3: Heterogeneous VM Topology
        createScreenshotSlide(ppt, 12, totalSlides, "MEMBER 4: LIVE SYSTEM WALKTHROUGH",
            "Heterogeneous Virtual Machine Cluster",
            "Specifications and dynamic utilization monitoring of the 4 cluster VMs.",
            SCREENSHOTS_DIR + "/vms.png",
            "Key Observations:\n• Heterogeneous node capacities: VM1 (4000 MIPS) down to VM4 (1000 MIPS).\n• Dual Processing Elements (PEs) and configured RAM allocation.\n• Dynamic CPU/Memory load markers tracking active task burdens.");

        // Slide 13: Member 5 - Output Screenshot 4: 3-Way Benchmark Comparison
        createScreenshotSlide(ppt, 13, totalSlides, "MEMBER 5: BENCHMARK EVALUATION",
            "Tri-Algorithm Benchmark Comparison",
            "Side-by-side empirical performance comparison across FCFS, Round Robin, and DTS.",
            SCREENSHOTS_DIR + "/comparison.png",
            "Key Observations:\n• Dynamic Task Scheduling drastically reduces cluster makespan (~14.5s vs ~138.8s).\n• Average waiting latency drops by >95% compared to FCFS.\n• CPU utilization reaches ~94%, eliminating idle compute resources.");

        // Slide 14: Member 5 - Quantitative Empirical Results Table
        createSlide14(ppt, 14, totalSlides);

        // Slide 15: Member 5 - Conclusion, Future Work & Viva Q&A Defense
        createSlide15(ppt, 15, totalSlides);

        // Ensure parent directory exists and write
        new File(OUTPUT_PPTX).getParentFile().mkdirs();
        try (FileOutputStream out = new FileOutputStream(OUTPUT_PPTX)) {
            ppt.write(out);
        }
        ppt.close();
    }

    // ==========================================
    // REUSABLE SLIDE PATTERN (HEADER + FOOTER)
    // ==========================================
    private static void applySlideTemplate(XSLFSlide slide, int slideNum, int totalSlides,
                                          String tag, String title, String subtitle, String presenter) {
        // Clean white background
        XSLFAutoShape bg = slide.createAutoShape();
        bg.setAnchor(new Rectangle(0, 0, WIDTH, HEIGHT));
        bg.setFillColor(BG_WHITE);

        // Top Header Logo Badge: [ CLOUDSIM DTS ]
        XSLFTextBox logoBox = slide.createTextBox();
        logoBox.setAnchor(new Rectangle(50, 24, 150, 26));
        logoBox.setFillColor(BADGE_BG);
        logoBox.setLineColor(BADGE_BORDER);
        XSLFTextParagraph logoPara = logoBox.addNewTextParagraph();
        logoPara.setTextAlign(TextAlign.CENTER);
        XSLFTextRun logoRun = logoPara.addNewTextRun();
        logoRun.setText("CLOUDSIM DTS");
        logoRun.setFontFamily(FONT_FAMILY);
        logoRun.setFontSize(10.0);
        logoRun.setBold(true);
        logoRun.setFontColor(TEXT_MAIN);

        // Tag / Category
        XSLFTextBox tagBox = slide.createTextBox();
        tagBox.setAnchor(new Rectangle(215, 26, 600, 22));
        XSLFTextParagraph tagPara = tagBox.addNewTextParagraph();
        XSLFTextRun tagRun = tagPara.addNewTextRun();
        tagRun.setText("•  " + tag.toUpperCase());
        tagRun.setFontFamily(FONT_FAMILY);
        tagRun.setFontSize(10.0);
        tagRun.setBold(true);
        tagRun.setFontColor(TEXT_SUBTLE);

        // Title
        XSLFTextBox titleBox = slide.createTextBox();
        titleBox.setAnchor(new Rectangle(50, 52, 1000, 40));
        XSLFTextParagraph titlePara = titleBox.addNewTextParagraph();
        XSLFTextRun titleRun = titlePara.addNewTextRun();
        titleRun.setText(title);
        titleRun.setFontFamily(FONT_FAMILY);
        titleRun.setFontSize(22.0);
        titleRun.setBold(true);
        titleRun.setFontColor(TEXT_MAIN);

        // Subtitle
        if (subtitle != null && !subtitle.isEmpty()) {
            XSLFTextBox subBox = slide.createTextBox();
            subBox.setAnchor(new Rectangle(50, 92, 1000, 24));
            XSLFTextParagraph subPara = subBox.addNewTextParagraph();
            XSLFTextRun subRun = subPara.addNewTextRun();
            subRun.setText(subtitle);
            subRun.setFontFamily(FONT_FAMILY);
            subRun.setFontSize(12.0);
            subRun.setFontColor(TEXT_MUTED);
        }

        // Header Divider Line
        XSLFAutoShape hLine = slide.createAutoShape();
        hLine.setAnchor(new Rectangle(50, 120, WIDTH - 100, 1));
        hLine.setFillColor(LINE_GRAY);

        // Footer Divider Line
        XSLFAutoShape fLine = slide.createAutoShape();
        fLine.setAnchor(new Rectangle(50, HEIGHT - 45, WIDTH - 100, 1));
        fLine.setFillColor(LINE_GRAY);

        // Footer Text Left
        XSLFTextBox fLeft = slide.createTextBox();
        fLeft.setAnchor(new Rectangle(50, HEIGHT - 40, 500, 24));
        XSLFTextParagraph fLeftPara = fLeft.addNewTextParagraph();
        XSLFTextRun fLeftRun = fLeftPara.addNewTextRun();
        fLeftRun.setText("Dynamic Task Scheduling in Cloud Computing  |  CloudSim Plus & Next.js");
        fLeftRun.setFontFamily(FONT_FAMILY);
        fLeftRun.setFontSize(10.0);
        fLeftRun.setFontColor(TEXT_SUBTLE);

        // Footer Center (Presenter)
        if (presenter != null && !presenter.isEmpty()) {
            XSLFTextBox fCenter = slide.createTextBox();
            fCenter.setAnchor(new Rectangle(WIDTH / 2 - 150, HEIGHT - 40, 300, 24));
            XSLFTextParagraph fCenterPara = fCenter.addNewTextParagraph();
            fCenterPara.setTextAlign(TextAlign.CENTER);
            XSLFTextRun fCenterRun = fCenterPara.addNewTextRun();
            fCenterRun.setText(presenter);
            fCenterRun.setFontFamily(FONT_FAMILY);
            fCenterRun.setFontSize(10.0);
            fCenterRun.setBold(true);
            fCenterRun.setFontColor(TEXT_MUTED);
        }

        // Footer Text Right (Slide Number)
        XSLFTextBox fRight = slide.createTextBox();
        fRight.setAnchor(new Rectangle(WIDTH - 200, HEIGHT - 40, 150, 24));
        XSLFTextParagraph fRightPara = fRight.addNewTextParagraph();
        fRightPara.setTextAlign(TextAlign.RIGHT);
        XSLFTextRun fRightRun = fRightPara.addNewTextRun();
        fRightRun.setText("Slide " + slideNum + " of " + totalSlides);
        fRightRun.setFontFamily(FONT_FAMILY);
        fRightRun.setFontSize(10.0);
        fRightRun.setFontColor(TEXT_SUBTLE);
    }

    // ==========================================
    // SLIDE BUILDERS
    // ==========================================

    private static void createTitleSlide(XMLSlideShow ppt, int slideNum, int totalSlides) {
        XSLFSlide slide = ppt.createSlide();

        // Background
        XSLFAutoShape bg = slide.createAutoShape();
        bg.setAnchor(new Rectangle(0, 0, WIDTH, HEIGHT));
        bg.setFillColor(BG_WHITE);

        // Border frame
        XSLFAutoShape frame = slide.createAutoShape();
        frame.setAnchor(new Rectangle(40, 40, WIDTH - 80, HEIGHT - 80));
        frame.setFillColor(BG_WHITE);
        frame.setLineColor(LINE_GRAY);

        // Logo pill top
        XSLFTextBox logoBox = slide.createTextBox();
        logoBox.setAnchor(new Rectangle(WIDTH / 2 - 100, 80, 200, 32));
        logoBox.setFillColor(BADGE_BG);
        logoBox.setLineColor(BADGE_BORDER);
        XSLFTextParagraph logoPara = logoBox.addNewTextParagraph();
        logoPara.setTextAlign(TextAlign.CENTER);
        XSLFTextRun logoRun = logoPara.addNewTextRun();
        logoRun.setText("CLOUDSIM DTS PROJECT");
        logoRun.setFontFamily(FONT_FAMILY);
        logoRun.setFontSize(11.0);
        logoRun.setBold(true);
        logoRun.setFontColor(TEXT_MAIN);

        // Main Title
        XSLFTextBox titleBox = slide.createTextBox();
        titleBox.setAnchor(new Rectangle(100, 130, WIDTH - 200, 110));
        XSLFTextParagraph titlePara = titleBox.addNewTextParagraph();
        titlePara.setTextAlign(TextAlign.CENTER);
        XSLFTextRun titleRun = titlePara.addNewTextRun();
        titleRun.setText("Performance Improvement in Cloud Computing Using Dynamic Task Scheduling Algorithm");
        titleRun.setFontFamily(FONT_FAMILY);
        titleRun.setFontSize(28.0);
        titleRun.setBold(true);
        titleRun.setFontColor(TEXT_MAIN);

        // Subtitle
        XSLFTextBox subBox = slide.createTextBox();
        subBox.setAnchor(new Rectangle(120, 245, WIDTH - 240, 50));
        XSLFTextParagraph subPara = subBox.addNewTextParagraph();
        subPara.setTextAlign(TextAlign.CENTER);
        XSLFTextRun subRun = subPara.addNewTextRun();
        subRun.setText("A Distributed Dual-Engine Academic Research System Combining Java CloudSim Plus Discrete-Event Simulation with a Next.js 16 Web Dashboard");
        subRun.setFontFamily(FONT_FAMILY);
        subRun.setFontSize(13.0);
        subRun.setFontColor(TEXT_MUTED);

        // Divider
        XSLFAutoShape div = slide.createAutoShape();
        div.setAnchor(new Rectangle(WIDTH / 2 - 150, 310, 300, 1));
        div.setFillColor(LINE_GRAY);

        // 5 Members presentation box
        XSLFTextBox teamBox = slide.createTextBox();
        teamBox.setAnchor(new Rectangle(80, 330, WIDTH - 160, 230));
        teamBox.setFillColor(BG_CARD);
        teamBox.setLineColor(LINE_GRAY);

        XSLFTextParagraph hPara = teamBox.addNewTextParagraph();
        hPara.setTextAlign(TextAlign.CENTER);
        XSLFTextRun hRun = hPara.addNewTextRun();
        hRun.setText("PROJECT PRESENTATION TEAM (5 MEMBERS)");
        hRun.setFontFamily(FONT_FAMILY);
        hRun.setFontSize(12.0);
        hRun.setBold(true);
        hRun.setFontColor(TEXT_MAIN);

        String[] members = {
            "Member 1: Project Overview, Problem Statement & Architecture",
            "Member 2: CloudSim Infrastructure Modeling & REST Bridge",
            "Member 3: Scheduling Algorithm Theory & Mathematical Metrics",
            "Member 4: Live Application Walkthrough & System Demonstration",
            "Member 5: Empirical Benchmark Results, Analysis & Defense"
        };

        for (String m : members) {
            XSLFTextParagraph p = teamBox.addNewTextParagraph();
            p.setSpaceBefore(6.0);
            p.setTextAlign(TextAlign.CENTER);
            XSLFTextRun r = p.addNewTextRun();
            r.setText(m);
            r.setFontFamily(FONT_FAMILY);
            r.setFontSize(11.0);
            r.setFontColor(TEXT_MUTED);
        }

        // Footer info
        XSLFTextBox footBox = slide.createTextBox();
        footBox.setAnchor(new Rectangle(100, 580, WIDTH - 200, 30));
        XSLFTextParagraph footPara = footBox.addNewTextParagraph();
        footPara.setTextAlign(TextAlign.CENTER);
        XSLFTextRun footRun = footPara.addNewTextRun();
        footRun.setText("Academic Capstone Project  |  Java 21 LTS  |  CloudSim Plus 8.5.7  |  Next.js 16  |  React 19");
        footRun.setFontFamily(FONT_FAMILY);
        footRun.setFontSize(10.0);
        footRun.setFontColor(TEXT_SUBTLE);
    }

    private static void createAgendaSlide(XMLSlideShow ppt, int slideNum, int totalSlides) {
        XSLFSlide slide = ppt.createSlide();
        applySlideTemplate(slide, slideNum, totalSlides, "EXECUTIVE AGENDA",
            "5-Member Presentation Agenda & Role Distribution",
            "Structured end-to-end presentation flow allocating technical ownership across all team members.",
            "All Members");

        String[][] cards = {
            {"Member 1: Overview & Architecture",
             "• Cloud computing context & heterogeneous node realities\n• Limitations of FCFS and Round Robin\n• Objectives: Makespan, Wait time, CPU utilization\n• Distributed Dual-Engine System Architecture"},
            {"Member 2: CloudSim Infrastructure",
             "• CloudSim Plus 8.5.7 discrete-event simulation model\n• Physical Datacenter (2 Hosts, 3000 MIPS each)\n• 4 Heterogeneous VMs (1000, 2000, 2000, 3000 MIPS)\n• Embedded HTTP REST Bridge on port 8080"},
            {"Member 3: Algorithms & Metrics",
             "• Mathematical models of FCFS and Round Robin\n• Proposed Dynamic Task Scheduling (DTS) heuristic\n• Minimum Completion Time (MCT) decision formula\n• Evaluation metrics: Makespan, Wait, Throughput, Gini"},
            {"Member 4: Live Demo & Walkthrough",
             "• Interactive Next.js 16 web dashboard tour\n• Task Scheduler execution & Gantt timeline\n• Real-time broker decision log inspection\n• Seamless one-click Dual-Engine toggle"},
            {"Member 5: Results & Defense",
             "• Tri-algorithm benchmark evaluation (25-300 tasks)\n• Empirical findings: ~89.5% Makespan reduction\n• CPU utilization jumps from 13.8% to 94.0%\n• Viva examiner defense and future research scope"}
        };

        int colWidth = 224;
        int gap = 15;
        int leftStart = 50;
        int top = 145;
        int height = 480;

        for (int i = 0; i < cards.length; i++) {
            int x = leftStart + i * (colWidth + gap);
            createCardBox(slide, x, top, colWidth, height, cards[i][0], cards[i][1]);
        }
    }

    private static void createSlide3(XMLSlideShow ppt, int slideNum, int totalSlides) {
        XSLFSlide slide = ppt.createSlide();
        applySlideTemplate(slide, slideNum, totalSlides, "MEMBER 1: PROJECT OVERVIEW",
            "Cloud Computing Context & The Task Scheduling Challenge",
            "Why task scheduling is the fundamental determinant of cluster efficiency and Quality of Service.",
            "Member 1");

        createCardBox(slide, 50, 145, 565, 230, "The Reality of Modern Cloud Datacenters",
            "• Heterogeneous Virtual Machines: Cloud providers (AWS, Azure, GCP) offer diverse VM types with varying MIPS, cores, and memory.\n\n"
            + "• Dynamic & Asynchronous Workloads: Client requests arrive non-deterministically with unpredictable Million Instruction (MI) lengths.\n\n"
            + "• Multi-Tenant Contention: Multiple tasks compete for shared host compute elements, requiring intelligent queue governance.");

        createCardBox(slide, 645, 145, 585, 230, "The Role of the Datacenter Broker",
            "• Central Scheduling Entity: Acts as the intelligent middleware mapping client Cloudlets to appropriate Virtual Machines.\n\n"
            + "• Scheduling Objective: Balance competing objectives—minimizing makespan and user latency while maximizing provider resource utilization.\n\n"
            + "• The Cost of Bad Decisions: Unbalanced mapping causes prolonged waiting, high idle costs, and SLA penalties.");

        createCardBox(slide, 50, 395, 1180, 230, "Project Research Objectives",
            "1. Minimize Cluster Makespan: Complete batch workloads in the shortest possible total simulation clock time.\n"
            + "2. Reduce Average Waiting Time: Eliminate queue starvation so client cloudlets start processing immediately.\n"
            + "3. Maximize Aggregate CPU Utilization: Keep high-capacity nodes saturated and eliminate idle processing element cycles.\n"
            + "4. Maintain Workload Load Balance: Distribute tasks proportionately according to node compute capacity (monitored via Gini index).\n"
            + "5. Deliver an Interactive GUI: Provide visual verification via a modern Next.js management console connected to Java CloudSim Plus.");
    }

    private static void createSlide4(XMLSlideShow ppt, int slideNum, int totalSlides) {
        XSLFSlide slide = ppt.createSlide();
        applySlideTemplate(slide, slideNum, totalSlides, "MEMBER 1: PROBLEM STATEMENT",
            "Problem Statement: Limitations of Conventional Schedulers",
            "Why classical FCFS and Round Robin fail in heterogeneous cloud computing environments.",
            "Member 1");

        createCardBox(slide, 50, 145, 565, 480, "First Come First Serve (FCFS) Bottlenecks",
            "• Complete Blindness to Node Heterogeneity:\n"
            + "  Tasks are queued strictly in order of arrival and assigned to the first node or default VM without considering processing speed.\n\n"
            + "• Convoy Effect:\n"
            + "  A massive 15,000 MI task scheduled first blocks the execution channel, forcing subsequent lightweight 1,000 MI tasks to wait hundreds of seconds.\n\n"
            + "• Severe Resource Underutilization:\n"
            + "  In our benchmarks, FCFS left 3 out of 4 VMs idle during major processing phases, resulting in a dismal 13.8% cluster CPU utilization.\n\n"
            + "• Long Makespan:\n"
            + "  Total execution time reaches ~138.8 seconds on 12 tasks.");

        createCardBox(slide, 645, 145, 585, 480, "Round Robin (RR) Limitations",
            "• Cyclic Distribution Ignores Processing Capacity:\n"
            + "  Round Robin cycles through VMs (VM0 -> VM1 -> VM2 -> VM3 -> VM0), treating a 1,000 MIPS entry node identically to a 3,000 MIPS compute node.\n\n"
            + "• Heavy Job Trapping:\n"
            + "  A heavy task assigned to the slowest VM becomes the cluster bottleneck (straggler), prolonging overall makespan to ~28.1 seconds.\n\n"
            + "• Suboptimal Waiting Times:\n"
            + "  Fast VMs complete their tasks early and sit idle while slow VMs struggle with deep residual queues.\n\n"
            + "• Need for Dynamic Heuristic:\n"
            + "  The cloud requires state-aware scheduling that calculates projected completion times before every dispatch.");
    }

    private static void createSlide5(XMLSlideShow ppt, int slideNum, int totalSlides) {
        XSLFSlide slide = ppt.createSlide();
        applySlideTemplate(slide, slideNum, totalSlides, "MEMBER 1: SYSTEM ARCHITECTURE",
            "Distributed Dual-Engine System Architecture",
            "High-level interaction between the Java discrete-event backend and the Next.js web console.",
            "Member 1");

        createCardBox(slide, 50, 145, 360, 480, "1. Next.js Web Dashboard\n(Port 3000)",
            "• Technologies:\n"
            + "  Next.js 16.4.0 (Turbopack)\n"
            + "  React 19.3.0 Client Components\n"
            + "  TypeScript 5 Strict Mode\n"
            + "  Tailwind CSS v4 & Recharts\n\n"
            + "• Core Responsibilities:\n"
            + "  - Visual Task Management Workstation\n"
            + "  - Real-time Gantt Execution Timeline\n"
            + "  - Step-by-step Broker Decision Stream\n"
            + "  - 3-Way Comparative Analytics\n"
            + "  - Automatic Backend Health Polling\n"
            + "  - Built-in Fallback TypeScript Engine");

        createCardBox(slide, 430, 145, 380, 480, "2. RESTful HTTP Bridge\n(Port 8080)",
            "• Lightweight Server:\n"
            + "  Built using JDK com.sun.net.httpserver\n"
            + "  Zero additional external dependencies\n"
            + "  Multi-threaded executor pool (4 threads)\n\n"
            + "• REST API Endpoints:\n"
            + "  - GET /api/health:\n"
            + "    Server status, Java version, VM specs\n"
            + "  - POST /api/simulate:\n"
            + "    Executes single algorithm simulation\n"
            + "  - POST /api/compare:\n"
            + "    Concurrent FCFS vs RR vs DTS benchmark\n\n"
            + "• Full CORS Support enabled");

        createCardBox(slide, 830, 145, 400, 480, "3. CloudSim Plus Simulation Engine\n(Java 21 LTS)",
            "• Discrete-Event Framework:\n"
            + "  CloudSim Plus 8.5.7\n\n"
            + "• Simulated Datacenter Elements:\n"
            + "  - 1 Physical Datacenter\n"
            + "  - 2 Physical Hosts (3000 MIPS x 2 PEs)\n"
            + "  - 4 Heterogeneous VMs (1000-3000 MIPS)\n"
            + "  - SpaceShared Cloudlet Scheduler\n\n"
            + "• Custom Scheduling Broker:\n"
            + "  Identity-mapped DatacenterBroker\n"
            + "  enforcing algorithm assignments\n\n"
            + "• Metrics Calculator:\n"
            + "  Generates Makespan, Wait, CPU Util, Gini");
    }

    private static void createSlide6(XMLSlideShow ppt, int slideNum, int totalSlides) {
        XSLFSlide slide = ppt.createSlide();
        applySlideTemplate(slide, slideNum, totalSlides, "MEMBER 2: CLOUDSIM INFRASTRUCTURE",
            "CloudSim Plus Simulation Modeling & VM Topology",
            "Detailed hardware specifications and multi-tier virtualization modeling in SimulationEnvironment.java.",
            "Member 2");

        createCardBox(slide, 50, 145, 565, 230, "Physical Datacenter Modeling",
            "• Datacenter Architecture: 1 Datacenter Simple managing 2 Physical Hosts.\n\n"
            + "• Host Specifications:\n"
            + "  - Dual Processing Elements (PEs) per host: 3,000 MIPS per PE.\n"
            + "  - RAM: 16,384 MB (16 GB) high-speed host memory.\n"
            + "  - Bandwidth: 10,000 Mbps (10 Gbps) internal fabric.\n"
            + "  - Storage: 1,000,000 MB (1 TB) local storage per host.");

        createCardBox(slide, 645, 145, 585, 230, "Heterogeneous Virtual Machine Topology",
            "• VM 0 (Entry Node): 1,000 MIPS, 2 PEs, 2,048 MB RAM, 1 Gbps BW\n\n"
            + "• VM 1 (Standard Node): 2,000 MIPS, 2 PEs, 2,048 MB RAM, 1 Gbps BW\n\n"
            + "• VM 2 (Balanced Node): 2,000 MIPS, 2 PEs, 2,048 MB RAM, 1 Gbps BW\n\n"
            + "• VM 3 (High-Compute Node): 3,000 MIPS, 2 PEs, 2,048 MB RAM, 1 Gbps BW\n\n"
            + "Total Cluster Processing Capacity: 16,000 Aggregate MIPS across 8 PEs.");

        createCardBox(slide, 50, 395, 1180, 230, "Task Scheduling Policy & Cloudlet Parameters",
            "• SpaceShared Execution Model: Every VM uses CloudletSchedulerSpaceShared. When multiple tasks are assigned to a VM, they queue and execute sequentially on the VM's PEs, reflecting realistic queue delays.\n\n"
            + "• Cloudlet Workload Parameters: Task sizes range from 1,000 MI to 20,000 MI. Each task requires 1 PE, 300 MB file size, and 300 MB output size.\n\n"
            + "• Custom SchedulingBroker: Overrides default CloudSim round-robin mapping by strictly enforcing scheduler assignments through an identity-keyed mapping table.");
    }

    private static void createSlide7(XMLSlideShow ppt, int slideNum, int totalSlides) {
        XSLFSlide slide = ppt.createSlide();
        applySlideTemplate(slide, slideNum, totalSlides, "MEMBER 2: CLOUDSIM INFRASTRUCTURE",
            "REST Bridge Server & Workload Modeling",
            "How SimulationServer.java exposes real-time simulation capabilities over JSON REST endpoints.",
            "Member 2");

        createCardBox(slide, 50, 145, 565, 480, "HTTP REST Bridge Server Mechanics",
            "• Architecture: Lightweight, standalone Java HTTP daemon running on port 8080.\n\n"
            + "• GET /api/health:\n"
            + "  Returns operational status, framework version ('CloudSim Plus'), active JDK ('21.0.10'), VM MIPS array, and default workload sizes.\n\n"
            + "• POST /api/simulate:\n"
            + "  Accepts JSON payload containing algorithm ('fcfs', 'rr', 'dynamic') and task lengths. Executes CloudSim Plus simulation and returns full execution logs.\n\n"
            + "• POST /api/compare:\n"
            + "  Executes FCFS, Round Robin, and DTS concurrently on the same workload. Returns comparative metrics in a single structured payload.\n\n"
            + "• Cross-Origin Resource Sharing (CORS):\n"
            + "  Configured with Access-Control-Allow-Origin: * for secure web client communication.");

        createCardBox(slide, 645, 145, 585, 480, "Deterministic Workload Generation",
            "• Reproducible Task Generation (WorkloadGenerator.java):\n"
            + "  Uses Java Random initialized with a deterministic seed (default: 42L) to guarantee exact reproducibility across comparative runs.\n\n"
            + "• Realistic Length Distribution:\n"
            + "  Task lengths are generated uniformly between 5,000 MI and 20,000 MI, reflecting varied cloud workloads (microservices, batch jobs, data processing).\n\n"
            + "• Standard Benchmark Batches:\n"
            + "  Configured for 25, 100, and 300 task batches in academic evaluation.\n\n"
            + "• Automated CSV Persistence (MetricsCalculator.java):\n"
            + "  Every completed execution automatically appends a formatted row to results/results.csv for auditing.");
    }

    private static void createSlide8(XMLSlideShow ppt, int slideNum, int totalSlides) {
        XSLFSlide slide = ppt.createSlide();
        applySlideTemplate(slide, slideNum, totalSlides, "MEMBER 3: ALGORITHM THEORY",
            "Scheduling Algorithm Mechanics: Baselines vs Dynamic DTS",
            "Theoretical formulation of the proposed Minimum Completion Time (MCT) heuristic.",
            "Member 3");

        createCardBox(slide, 50, 145, 360, 480, "1. First Come First Serve (FCFS)",
            "• Selection Formula:\n"
            + "  Assign task T_i to VM with earliest availableTime:\n"
            + "  VM = argmin(VM_j.availableTime)\n\n"
            + "• Algorithmic Flaw:\n"
            + "  Ignores task execution length and VM processing MIPS entirely.\n\n"
            + "• Consequence:\n"
            + "  Creates severe convoy bottlenecks and leaves high-speed nodes idle.");

        createCardBox(slide, 430, 145, 380, 480, "2. Round Robin (RR)",
            "• Selection Formula:\n"
            + "  Target VM = turnIndex % N_VMs\n"
            + "  turnIndex = (turnIndex + 1) % N_VMs\n\n"
            + "• Algorithmic Flaw:\n"
            + "  Treats heterogeneous nodes symmetrically, sending heavy jobs to slow VMs.\n\n"
            + "• Consequence:\n"
            + "  Slow nodes become cluster stragglers, driving up total makespan.");

        createCardBox(slide, 830, 145, 400, 480, "3. Proposed Dynamic DTS Heuristic",
            "• Minimum Completion Time Principle:\n"
            + "  For each incoming task T_i:\n\n"
            + "  Step 1: Compute Ready Time:\n"
            + "  Ready_j = max(VM_j.availableTime, Arrival_i)\n\n"
            + "  Step 2: Estimate Execution Time:\n"
            + "  ExecTime_j = Length_i / (MIPS_j * PEs_j)\n\n"
            + "  Step 3: Predict Finish Time:\n"
            + "  ProjectedFinish_j = Ready_j + ExecTime_j\n\n"
            + "  Step 4: Select Optimal Node:\n"
            + "  Target = argmin(ProjectedFinish_j)\n\n"
            + "• Complexity: O(N * M) where N = tasks, M = VMs. Executes in <2ms.");
    }

    private static void createSlide9(XMLSlideShow ppt, int slideNum, int totalSlides) {
        XSLFSlide slide = ppt.createSlide();
        applySlideTemplate(slide, slideNum, totalSlides, "MEMBER 3: EVALUATION METRICS",
            "Performance Evaluation Metrics & Formulas",
            "The five core quantitative metrics used to evaluate and compare scheduling algorithms.",
            "Member 3");

        createCardBox(slide, 50, 145, 565, 230, "1. Makespan & Throughput",
            "• Makespan (M): Total elapsed time from start of first task to completion of last task.\n"
            + "  Formula: Makespan = max(FinishTime_i) - min(StartTime_i)\n"
            + "  Objective: Minimize Makespan (lower is better).\n\n"
            + "• Throughput (TP): Number of completed cloudlets per simulation second.\n"
            + "  Formula: TP = TotalCompletedTasks / Makespan\n"
            + "  Objective: Maximize Throughput (higher is better).");

        createCardBox(slide, 645, 145, 585, 230, "2. Average Waiting & Response Time",
            "• Average Waiting Time (AWT): Queue delay before task begins processing.\n"
            + "  Formula: AWT = (1 / N) * sum(StartTime_i - ArrivalTime_i)\n"
            + "  Objective: Minimize Waiting Time (lower is better).\n\n"
            + "• Average Response Time (ART): Total duration from arrival to completion.\n"
            + "  Formula: ART = (1 / N) * sum(FinishTime_i - ArrivalTime_i)\n"
            + "  Objective: Minimize Response Time (lower is better).");

        createCardBox(slide, 50, 395, 1180, 230, "3. CPU Utilization & Gini Coefficient (Load Balance)",
            "• Aggregate CPU Utilization (U_cpu):\n"
            + "  Formula: U_cpu = sum(BusyTime_j * MIPS_j) / (Makespan * TotalMipsCapacity)\n"
            + "  Measures what percentage of total cluster computational capacity was actively performing work (higher is better).\n\n"
            + "• Gini Coefficient of Load Imbalance (G):\n"
            + "  Formula: G = sum_i sum_j |Tasks_i - Tasks_j| / (2 * M * sum_k Tasks_k)\n"
            + "  A value of 0 indicates perfectly equal task distribution; 1 indicates extreme imbalance. DTS balances tasks according to VM processing power.");
    }

    private static void createScreenshotSlide(XMLSlideShow ppt, int slideNum, int totalSlides,
                                              String tag, String title, String subtitle,
                                              String imagePath, String observations) {
        XSLFSlide slide = ppt.createSlide();
        applySlideTemplate(slide, slideNum, totalSlides, tag, title, subtitle, "Member 4");

        // Left box: Observation card
        createCardBox(slide, 50, 145, 420, 480, "Operational Analysis & Observations", observations);

        // Right box: Picture frame & image
        XSLFAutoShape picFrame = slide.createAutoShape();
        picFrame.setAnchor(new Rectangle(490, 145, 740, 480));
        picFrame.setFillColor(BG_CARD);
        picFrame.setLineColor(LINE_GRAY);

        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            try {
                byte[] bytes = Files.readAllBytes(imgFile.toPath());
                XSLFPictureData picData = ppt.addPicture(bytes, PictureData.PictureType.PNG);
                XSLFPictureShape pic = slide.createPicture(picData);
                // Inside frame padding
                pic.setAnchor(new Rectangle(495, 150, 730, 470));
            } catch (Exception e) {
                System.err.println("Failed to embed screenshot: " + imagePath + " (" + e.getMessage() + ")");
            }
        } else {
            XSLFTextBox placeholder = slide.createTextBox();
            placeholder.setAnchor(new Rectangle(520, 350, 680, 50));
            XSLFTextParagraph p = placeholder.addNewTextParagraph();
            p.setTextAlign(TextAlign.CENTER);
            XSLFTextRun r = p.addNewTextRun();
            r.setText("[ Screenshot Image: " + imgFile.getName() + " ]");
            r.setFontFamily(FONT_FAMILY);
            r.setFontSize(14.0);
            r.setFontColor(TEXT_SUBTLE);
        }
    }

    private static void createSlide14(XMLSlideShow ppt, int slideNum, int totalSlides) {
        XSLFSlide slide = ppt.createSlide();
        applySlideTemplate(slide, slideNum, totalSlides, "MEMBER 5: EMPIRICAL BENCHMARK RESULTS",
            "Quantitative Performance Evaluation & Results Analysis",
            "Empirical results obtained across 25, 100, and 300 task batches running on CloudSim Plus.",
            "Member 5");

        // Table
        XSLFTable table = slide.createTable();
        table.setAnchor(new Rectangle(50, 145, 1180, 240));

        String[][] tableData = {
            {"Workload Batch", "Algorithm", "Makespan (s)", "Avg Wait (s)", "Throughput (t/s)", "CPU Util (%)", "Improvement vs FCFS"},
            {"25 Tasks", "FCFS", "164.22 s", "76.40 s", "0.152", "24.7 %", "Baseline"},
            {"25 Tasks", "Round Robin", "49.10 s", "9.22 s", "0.509", "83.1 %", "70.1% Makespan reduction"},
            {"25 Tasks", "Dynamic (DTS)", "23.57 s", "6.98 s", "1.061", "94.0 %", "85.6% Makespan reduction"},
            {"100 Tasks", "FCFS", "648.12 s", "322.58 s", "0.154", "24.6 %", "Baseline"},
            {"100 Tasks", "Round Robin", "175.32 s", "45.61 s", "0.570", "91.4 %", "73.0% Makespan reduction"},
            {"100 Tasks", "Dynamic (DTS)", "85.68 s", "38.28 s", "1.167", "96.6 %", "86.8% Makespan reduction"},
            {"300 Tasks", "FCFS", "1971.29 s", "978.27 s", "0.152", "24.7 %", "Baseline"},
            {"300 Tasks", "Round Robin", "524.18 s", "143.12 s", "0.572", "93.3 %", "73.4% Makespan reduction"},
            {"300 Tasks", "Dynamic (DTS)", "253.75 s", "121.41 s", "1.182", "97.5 %", "87.1% Makespan reduction"}
        };

        for (int r = 0; r < tableData.length; r++) {
            XSLFTableRow row = table.addRow();
            row.setHeight(r == 0 ? 28 : 22);
            for (int c = 0; c < tableData[r].length; c++) {
                XSLFTableCell cell = row.addCell();
                cell.setFillColor(r == 0 ? BADGE_BG : (r % 2 == 1 ? BG_WHITE : BG_CARD));
                XSLFTextParagraph p = cell.addNewTextParagraph();
                if (c == 0 || c == 1 || c == 6) p.setTextAlign(TextAlign.LEFT);
                else p.setTextAlign(TextAlign.CENTER);
                XSLFTextRun run = p.addNewTextRun();
                run.setText(tableData[r][c]);
                run.setFontFamily(FONT_FAMILY);
                run.setFontSize(r == 0 ? 10.5 : 10.0);
                if (r == 0 || (c == 1 && tableData[r][1].contains("Dynamic"))) run.setBold(true);
                run.setFontColor(TEXT_MAIN);
            }
        }

        // Summary callouts below table
        createCardBox(slide, 50, 405, 380, 220, "1. Makespan Reduction",
            "• Consistent ~85% to 89% reduction in cluster makespan compared to FCFS across all workload sizes.\n"
            + "• Dynamic DTS finished 300 tasks in 253.7s vs 1971.3s for FCFS.");

        createCardBox(slide, 450, 405, 380, 220, "2. CPU Utilization",
            "• Cluster CPU utilization jumps from 24.7% (FCFS) to >96% under Dynamic scheduling.\n"
            + "• Eliminates stranded high-speed compute cores.");

        createCardBox(slide, 850, 405, 380, 220, "3. Latency & Throughput",
            "• Task waiting delays decrease by up to 95.9%.\n"
            + "• Throughput increases by 7.7x (from 0.152 to 1.182 tasks/sec on 300 tasks).");
    }

    private static void createSlide15(XMLSlideShow ppt, int slideNum, int totalSlides) {
        XSLFSlide slide = ppt.createSlide();
        applySlideTemplate(slide, slideNum, totalSlides, "MEMBER 5: CONCLUSION & DEFENSE",
            "Conclusion, Future Research & Viva Q&A Defense",
            "Final project summary, academic deliverables, and anticipation of evaluator questions.",
            "Member 5");

        createCardBox(slide, 50, 145, 565, 480, "Project Conclusion & Deliverables",
            "• Proven Research Heuristic:\n"
            + "  Demonstrated empirically that state-aware Minimum Completion Time scheduling solves the heterogeneity bottleneck in cloud datacenters.\n\n"
            + "• Complete Dual-Engine System Delivered:\n"
            + "  - CloudSim Plus 8.5.7 discrete-event simulation engine.\n"
            + "  - Embedded HTTP REST bridge (port 8080).\n"
            + "  - Modern Next.js 16 Web Dashboard (port 3000).\n"
            + "  - Automated CSV results logging.\n\n"
            + "• Future Research Extensions:\n"
            + "  1. Multi-Objective Cost Optimization: Incorporating dollar costs per VM hour (AWS EC2 pricing models).\n"
            + "  2. Energy & Carbon Aware Scheduling: Dynamically consolidating tasks to power down idle hosts.\n"
            + "  3. Preemptive Task Scheduling: Enabling priority task preemption and VM live migration.");

        createCardBox(slide, 645, 145, 585, 480, "Key Viva / Examiner Defense Points",
            "• Q: Why CloudSim instead of real AWS/GCP?\n"
            + "  A: Controlled, deterministic reproducibility. Real clouds suffer from unpredictable network jitter and high financial costs.\n\n"
            + "• Q: What is the computational complexity of DTS?\n"
            + "  A: O(N * M) where N = tasks and M = VMs. Evaluating 4 to 64 VMs takes under 2 milliseconds, making it ideal for real-time cloud brokers.\n\n"
            + "• Q: What happens if the Java backend goes offline?\n"
            + "  A: The frontend automatically detects disconnection and seamlessly switches to the internal TypeScript simulation engine without crashing.\n\n"
            + "• Q: How is load balancing verified?\n"
            + "  A: Through the Gini coefficient over tasks per VM, proving that high-speed nodes handle proportionally more work without starvation.");
    }

    // ==========================================
    // UTILITY: CARD BOX GENERATOR
    // ==========================================
    private static void createCardBox(XSLFSlide slide, int x, int y, int w, int h, String header, String body) {
        XSLFAutoShape box = slide.createAutoShape();
        box.setAnchor(new Rectangle(x, y, w, h));
        box.setFillColor(BG_CARD);
        box.setLineColor(LINE_GRAY);

        XSLFTextBox textBox = slide.createTextBox();
        textBox.setAnchor(new Rectangle(x + 12, y + 10, w - 24, h - 20));

        // Header
        XSLFTextParagraph hPara = textBox.addNewTextParagraph();
        XSLFTextRun hRun = hPara.addNewTextRun();
        hRun.setText(header);
        hRun.setFontFamily(FONT_FAMILY);
        hRun.setFontSize(12.0);
        hRun.setBold(true);
        hRun.setFontColor(TEXT_MAIN);

        // Body
        if (body != null && !body.isEmpty()) {
            XSLFTextParagraph bPara = textBox.addNewTextParagraph();
            bPara.setSpaceBefore(8.0);
            XSLFTextRun bRun = bPara.addNewTextRun();
            bRun.setText(body);
            bRun.setFontFamily(FONT_FAMILY);
            bRun.setFontSize(10.5);
            bRun.setFontColor(TEXT_MUTED);
        }
    }
}

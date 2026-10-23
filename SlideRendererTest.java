import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SlideRendererTest {
    public static void main(String[] args) throws Exception {
        int width = 4800;
        int height = 2700;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // High quality rendering hints
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        // Background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Header Title
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 130));
        g.drawString("Team Details", 240, 240);

        // Draw VIT-AP Logo
        File logoFile = new File("C:/tools/cloudsim-dynamic-scheduler/docs/vit_logo.png");
        if (logoFile.exists()) {
            BufferedImage logo = ImageIO.read(logoFile);
            g.drawImage(logo, 4440, 80, 210, 220, null);
        }

        // Horizontal divider line
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(6));
        g.drawLine(230, 390, 4650, 390);

        // Cards: 2 on top, 2 in middle, 1 centered below
        drawCard(g, 360, 520, 1950, 430, "Member 1", "B.Anvith Bikshu", "Cloud Environment Setup", "CloudSim Plus, Data Centers, Hosts & Heterogeneous VMs");
        drawCard(g, 2490, 520, 1950, 430, "Member 2", "Yaswanth Reddy", "Task Management & Workload", "Cloudlet Generation, Dynamic Bursts & Workload Profiles");
        
        drawCard(g, 360, 1070, 1950, 430, "Member 3", "V.Satwik Sai", "Dynamic Scheduler Algorithm", "Real-Time VM Load Monitoring & Min-Completion Heuristic");
        drawCard(g, 2490, 1070, 1950, 430, "Member 4", "Nithin Sai Baba", "Benchmarking & Baseline Models", "FCFS and Round Robin Implementations & Comparative Engine");

        drawCard(g, 1425, 1620, 1950, 430, "Member 5", "P. Ram Arun Tej", "Performance Analysis & Evaluation", "Makespan, Waiting Time, Resource Utilization & Verification");

        // Slide number
        g.setFont(new Font("Arial", Font.PLAIN, 46));
        g.drawString("2", 4500, 2580);

        g.dispose();

        File out = new File("C:/tools/cloudsim-dynamic-scheduler/docs/test_slide_rendered.png");
        ImageIO.write(img, "png", out);
        System.out.println("Rendered slide: " + out.getAbsolutePath() + " (" + out.length() + " bytes)");
    }

    private static void drawCard(Graphics2D g, int x, int y, int w, int h, String line1, String line2, String line3, String line4) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(6));
        g.drawRect(x, y, w, h);

        g.setFont(new Font("Arial", Font.BOLD, 68));
        drawCentered(g, line1, x, y + 90, w);

        g.setFont(new Font("Arial", Font.BOLD, 88));
        drawCentered(g, line2, x, y + 200, w);

        g.setFont(new Font("Arial", Font.PLAIN, 52));
        drawCentered(g, line3, x, y + 295, w);

        g.setFont(new Font("Arial", Font.PLAIN, 44));
        g.setColor(new Color(60, 60, 60));
        drawCentered(g, line4, x, y + 365, w);
    }

    private static void drawCentered(Graphics2D g, String text, int x, int y, int w) {
        FontMetrics fm = g.getFontMetrics();
        int strW = fm.stringWidth(text);
        g.drawString(text, x + (w - strW) / 2, y);
    }
}

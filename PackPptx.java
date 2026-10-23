import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

public class PackPptx {
    public static void main(String[] args) throws Exception {
        Path sourceDir = Paths.get("C:/tools/cloudsim-dynamic-scheduler/docs/user_orig_extracted");
        Path zipFile = Paths.get("C:/tools/cloudsim-dynamic-scheduler/docs/Dynamic_Task_Scheduler.pptx");
        
        Files.deleteIfExists(zipFile);
        
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile.toFile()))) {
            Files.walk(sourceDir).forEach(path -> {
                if (!Files.isDirectory(path)) {
                    String relativePath = sourceDir.relativize(path).toString().replace('\\', '/');
                    try {
                        ZipEntry entry = new ZipEntry(relativePath);
                        zos.putNextEntry(entry);
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        System.out.println("Zipped PPTX size: " + Files.size(zipFile) + " bytes");
    }
}

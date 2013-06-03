package au.edu.uq.cmm.paul.queue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import au.edu.uq.cmm.paul.PaulConfiguration;

public class QueueFileManagerTestBase {

    protected static Path archiveDir;
    protected static Path captureDir;
    private static Path sourceDir;
    protected static File[] sourceFiles;

    @BeforeClass
    public static void setup() throws IOException {
    	archiveDir = Files.createTempDirectory("archive");
    	captureDir = Files.createTempDirectory("capture");
    	sourceDir = Files.createTempDirectory("source");
    	sourceFiles = new File[4];
    	for (int i = 0; i < sourceFiles.length; i++) {
    		sourceFiles[i] = new File(sourceDir.toFile(), 
    				"test-" + (i + 1) + ".txt");
    		try (OutputStream os = 
    				new FileOutputStream(sourceFiles[i])) {
    			os.write(("Test file #" + (i + 1) + "\n").getBytes());
    		} 
    	}
    }

    @AfterClass
    public static void teardown() throws IOException {
    	removeTree(archiveDir);
    	removeTree(captureDir);
    	removeTree(sourceDir);
    }

    private static void removeTree(Path dir) throws IOException {
    	Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
    		@Override
    		public FileVisitResult visitFile(Path file,
    				BasicFileAttributes attrs) throws IOException {
    			Files.delete(file);
    			return FileVisitResult.CONTINUE;
    		}
    		@Override
    		public FileVisitResult postVisitDirectory(Path dir, IOException exc)
    				throws IOException {
    			Files.delete(dir);
    			return FileVisitResult.CONTINUE;
    		}
    	});
    }

    public QueueFileManagerTestBase() {
        super();
    }

    protected PaulConfiguration buildConfig() {
    	PaulConfiguration config = new PaulConfiguration();
    	config.setCaptureDirectory(captureDir.toString());
    	config.setArchiveDirectory(archiveDir.toString());
    	return config;
    }

}
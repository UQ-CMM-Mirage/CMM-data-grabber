package au.edu.uq.cmm.paul.queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import org.junit.Test;

import au.edu.uq.cmm.paul.PaulConfiguration;
import au.edu.uq.cmm.paul.PaulException;

public abstract class QueueFileManagerTestBase {

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

    @Test
    public final void testFileStatus() throws QueueFileException {
    	QueueFileManager qfm = instantiate();
    	assertEquals(QueueFileManager.FileStatus.NOT_OURS, qfm.getFileStatus(sourceFiles[0]));
    }

    @Test
    public final void testEnqueueText() throws QueueFileException,
    InterruptedException {
        QueueFileManager qfm = instantiate();
        File file = qfm.generateUniqueFile("foop", false);
        qfm.enqueueFile("content\n", file, false);
        assertEquals(QueueFileManager.FileStatus.CAPTURED_FILE, qfm.getFileStatus(file));
        assertEquals(8L, file.length());
        try {
            qfm.enqueueFile("content\n", file, false);
            fail("no exception thrown");
        } catch (QueueFileException ex) {
            /**/
        }
        try {
            qfm.enqueueFile("content\n", file, true);
        } catch (QueueFileException ex) {
            fail("exception thrown");
        }
    }
    
    public final QueueFileManager instantiate() {
        return instantiate(buildConfig());
    }

    public abstract QueueFileManager instantiate(PaulConfiguration config);

    @Test
    public final void instantiateTest() {
        PaulConfiguration config = buildConfig();
    	instantiate(config);
    	try {
    		config.setArchiveDirectory("/fubar");
    		instantiate(config);
    		fail("Missing directory not diagnosed");
    	} catch (PaulException ex) {
    		assertTrue(ex.getMessage().contains("archive"));
    	} finally {
    		config.setArchiveDirectory(archiveDir.toString());
    	}
    	try {
    		config.setCaptureDirectory("/fubar");
    		instantiate(config);
    		fail("Missing directory not diagnosed");
    	} catch (PaulException ex) {
    		assertTrue(ex.getMessage().contains("capture"));
    	} finally {
    		config.setArchiveDirectory(captureDir.toString());
    	}
    }
    
    

}
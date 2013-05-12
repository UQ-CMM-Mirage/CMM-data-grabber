/*
* Copyright 2013, CMM, University of Queensland.
*
* This file is part of Paul.
*
* Paul is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Paul is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Paul. If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.uq.cmm.paul.queue;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
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
import org.junit.Ignore;
import org.junit.Test;

import au.edu.uq.cmm.paul.PaulConfiguration;
import au.edu.uq.cmm.paul.PaulException;

@Ignore
public class CopyingQueueFileManagerTest {

	private static Path archiveDir;
	private static Path captureDir;
	private static Path sourceDir;
	private static File[] sourceFiles;
	
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

	@Test
	public void instantiateTest() {
		PaulConfiguration config = buildConfig();
		new CopyingQueueFileManager(config);
		try {
			config.setArchiveDirectory("/fubar");
			new CopyingQueueFileManager(config);
			fail("Missing directory not diagnosed");
		} catch (PaulException ex) {
			assertTrue(ex.getMessage().contains("archive"));
		} finally {
			config.setArchiveDirectory(archiveDir.toString());
		}
		try {
			config.setCaptureDirectory("/fubar");
			new CopyingQueueFileManager(config);
			fail("Missing directory not diagnosed");
		} catch (PaulException ex) {
			assertTrue(ex.getMessage().contains("capture"));
		} finally {
			config.setArchiveDirectory(captureDir.toString());
		}
	}
	
	@Test 
	public void testPredicates() {
		QueueFileManager qfm = new CopyingQueueFileManager(buildConfig());
		assertTrue(!qfm.isQueuedFile(sourceFiles[0]));
		assertTrue(!qfm.isCopiedFile(sourceFiles[0]));
		assertTrue(!qfm.isArchivedFile(sourceFiles[0]));
	}

	@Test 
	public void testEnqueueFile() throws QueueFileException, InterruptedException {
		QueueFileManager qfm = new CopyingQueueFileManager(buildConfig());
		File enqueued = qfm.enqueueFile(sourceFiles[0], ".txt", false);
		assertTrue(qfm.isQueuedFile(enqueued));
		assertTrue(qfm.isCopiedFile(enqueued));
		assertTrue(!qfm.isArchivedFile(enqueued));
		assertTrue(enqueued.exists());
		assertEquals(13L, enqueued.length());
		assertTrue(enqueued.toString().contains("/file-"));
		assertTrue(!enqueued.toString().contains("/regrabbed-"));
		enqueued = qfm.enqueueFile(sourceFiles[0], ".txt", true);
		assertTrue(!enqueued.toString().contains("/file-"));
		assertTrue(enqueued.toString().contains("/regrabbed-"));
	}

	@Test 
	public void testEnqueueText() throws QueueFileException, InterruptedException {
		QueueFileManager qfm = new CopyingQueueFileManager(buildConfig());
		File file = qfm.generateUniqueFile("foop", false);
		qfm.enqueueFile("content\n", file, false);
		assertTrue(qfm.isQueuedFile(file));
		assertTrue(qfm.isCopiedFile(file));
		assertTrue(!qfm.isArchivedFile(file));
		assertTrue(file.exists());
		assertEquals(8L, file.length());
		try {
			qfm.enqueueFile("content\n", file, false);
			fail("no exception thrown");
		} catch (QueueFileException ex) {
			/**/
		}try {
			qfm.enqueueFile("content\n", file, true);
		} catch (QueueFileException ex) {
			fail("exception thrown");
		}
	}

	@Test 
	public void testArchiveFile() throws QueueFileException, InterruptedException {
		QueueFileManager qfm = new CopyingQueueFileManager(buildConfig());
		File enqueued = qfm.enqueueFile(sourceFiles[1], ".txt", false);
		File archived = qfm.archiveFile(enqueued);
		assertTrue(!qfm.isQueuedFile(archived));
		assertTrue(qfm.isCopiedFile(archived));
		assertTrue(qfm.isArchivedFile(archived));
		assertTrue(archived.exists());
		assertEquals(13L, archived.length());
		
		assertTrue(qfm.isQueuedFile(enqueued));
		assertTrue(qfm.isCopiedFile(enqueued));
		assertTrue(!qfm.isArchivedFile(enqueued));
		assertTrue(!enqueued.exists());
	}

	@Test 
	public void testRemoveFile() throws QueueFileException, InterruptedException {
		QueueFileManager qfm = new CopyingQueueFileManager(buildConfig());
		File enqueued = qfm.enqueueFile(sourceFiles[2], ".txt", false);
		qfm.removeFile(enqueued);
		assertTrue(!enqueued.exists());
		
		try {
			qfm.removeFile(sourceFiles[2]);
			fail("no exception");
		} catch (QueueFileException ex) {
			assertTrue(ex.getMessage().contains("is not in the queue"));
		}
		try {
			qfm.removeFile(new File("/fubar"));
			fail("no exception");
		} catch (QueueFileException ex) {
			assertTrue(ex.getMessage().contains("no longer exists"));
		}
	}

	private PaulConfiguration buildConfig() {
		PaulConfiguration config = new PaulConfiguration();
		config.setCaptureDirectory(captureDir.toString());
		config.setArchiveDirectory(archiveDir.toString());
		return config;
	}

}

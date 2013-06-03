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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import au.edu.uq.cmm.paul.PaulConfiguration;
import au.edu.uq.cmm.paul.PaulException;

public class LinkingQueueFileManagerTest extends QueueFileManagerTestBase {

	@Test
	public void instantiateTest() {
		PaulConfiguration config = buildConfig();
		new LinkingQueueFileManager(config);
		try {
			config.setArchiveDirectory("/fubar");
			new LinkingQueueFileManager(config);
			fail("Missing directory not diagnosed");
		} catch (PaulException ex) {
			assertTrue(ex.getMessage().contains("archive"));
		} finally {
			config.setArchiveDirectory(archiveDir.toString());
		}
		try {
			config.setCaptureDirectory("/fubar");
			new LinkingQueueFileManager(config);
			fail("Missing directory not diagnosed");
		} catch (PaulException ex) {
			assertTrue(ex.getMessage().contains("capture"));
		} finally {
			config.setArchiveDirectory(captureDir.toString());
		}
	}
	
	@Test 
	public void testPredicates() throws QueueFileException {
		QueueFileManager qfm = new LinkingQueueFileManager(buildConfig());
		assertTrue(!qfm.isQueuedFile(sourceFiles[0]));
		assertTrue(!qfm.isCopiedFile(sourceFiles[0]));
		assertTrue(!qfm.isArchivedFile(sourceFiles[0]));
	}

	@Test 
	public void testEnqueueFile() throws QueueFileException, InterruptedException {
		QueueFileManager qfm = new LinkingQueueFileManager(buildConfig());
		File enqueued = qfm.enqueueFile(sourceFiles[0], ".txt", false);
		assertTrue(qfm.isQueuedFile(enqueued));
		assertTrue(!qfm.isCopiedFile(enqueued));
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
		QueueFileManager qfm = new LinkingQueueFileManager(buildConfig());
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
		}
		try {
			qfm.enqueueFile("content\n", file, true);
		} catch (QueueFileException ex) {
			fail("exception thrown");
		}
	}

	@Test 
	public void testArchiveFile() throws QueueFileException, InterruptedException {
		QueueFileManager qfm = new LinkingQueueFileManager(buildConfig());
		File enqueued = qfm.enqueueFile(sourceFiles[1], ".txt", false);
        assertTrue(qfm.isQueuedFile(enqueued));
        assertTrue(!qfm.isCopiedFile(enqueued));
        assertTrue(!qfm.isArchivedFile(enqueued));
        assertTrue(enqueued.exists());

        File archived = qfm.archiveFile(enqueued);
		assertTrue(!qfm.isQueuedFile(archived));
		assertTrue(qfm.isCopiedFile(archived));
		assertTrue(qfm.isArchivedFile(archived));
		assertTrue(archived.exists());
		assertEquals(13L, archived.length());
		
		assertTrue(qfm.isQueuedFile(enqueued));
		assertTrue(!qfm.isCopiedFile(enqueued));
		assertTrue(!qfm.isArchivedFile(enqueued));
		assertTrue(!enqueued.exists());
	}

	@Test 
	public void testRemoveFile() throws QueueFileException, InterruptedException {
		QueueFileManager qfm = new LinkingQueueFileManager(buildConfig());
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

}

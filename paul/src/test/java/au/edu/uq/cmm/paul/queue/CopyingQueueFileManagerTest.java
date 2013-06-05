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

public class CopyingQueueFileManagerTest extends QueueFileManagerTestBase {

	@Test 
	public void testEnqueueFile() throws QueueFileException, InterruptedException {
		QueueFileManager qfm = instantiate();
		File enqueued = qfm.enqueueFile(sourceFiles[0], ".txt", false);
		assertEquals(QueueFileManager.FileStatus.CAPTURED_FILE, qfm.getFileStatus(enqueued));
		assertEquals(13L, enqueued.length());
		assertTrue(enqueued.toString().contains("/file-"));
		assertTrue(!enqueued.toString().contains("/regrabbed-"));
		enqueued = qfm.enqueueFile(sourceFiles[0], ".txt", true);
		assertTrue(!enqueued.toString().contains("/file-"));
		assertTrue(enqueued.toString().contains("/regrabbed-"));
	}

	@Test 
	public void testArchiveFile() throws QueueFileException, InterruptedException {
		QueueFileManager qfm = instantiate();
        assertEquals(QueueFileManager.FileStatus.NOT_OURS, qfm.getFileStatus(sourceFiles[1]));
		File enqueued = qfm.enqueueFile(sourceFiles[1], ".txt", false);
        assertEquals(QueueFileManager.FileStatus.CAPTURED_FILE, qfm.getFileStatus(enqueued));
		File archived = qfm.archiveFile(enqueued);
		assertEquals(QueueFileManager.FileStatus.ARCHIVED_FILE, qfm.getFileStatus(archived));
		assertTrue(archived.exists());
		assertEquals(QueueFileManager.FileStatus.NON_EXISTENT, qfm.getFileStatus(enqueued));
	}

	@Test 
	public void testRemoveFile() throws QueueFileException, InterruptedException {
		QueueFileManager qfm = instantiate();
		File enqueued = qfm.enqueueFile(sourceFiles[2], ".txt", false);
        assertEquals(QueueFileManager.FileStatus.CAPTURED_FILE, qfm.getFileStatus(enqueued));
		qfm.removeFile(enqueued);
        assertEquals(QueueFileManager.FileStatus.NON_EXISTENT, qfm.getFileStatus(enqueued));
		
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

    @Override
    public QueueFileManager instantiate(PaulConfiguration config) {
        return new CopyingQueueFileManager(config);
    }

}

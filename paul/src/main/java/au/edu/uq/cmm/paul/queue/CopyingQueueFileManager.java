/*
* Copyright 2012-2013, CMM, University of Queensland.
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

import java.io.File;
import java.nio.file.Files;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.paul.PaulConfiguration;

/**
 * This queue file manager saves a private copy of each file to the queue area.
 * 
 * @author scrawley
 *
 */
public class CopyingQueueFileManager extends AbstractQueueFileManager implements QueueFileManager {
    static final Logger LOG = LoggerFactory.getLogger(CopyingQueueFileManager.class);
    
    public CopyingQueueFileManager(PaulConfiguration config) {
        super(config, LOG);
    }

	@Override
    public File enqueueFile(File source, String suffix, boolean regrabbing) 
                throws QueueFileException, InterruptedException {
        // TODO - if the time taken to copy files is a problem, we could 
        // potentially improve this by using NIO or memory mapped files.
        File target = generateUniqueFile(suffix, regrabbing);
        return copyFile(source, target, "queue");
    }

    @Override
    public File renameGrabbedDatafile(File file) throws QueueFileException {
        String extension = FilenameUtils.getExtension(file.toString());
        if (!extension.isEmpty()) {
            extension = "." + extension;
        }
        for (int i = 0; i < RETRY; i++) {
            File newFile = generateUniqueFile(extension, false);
            if (!file.renameTo(newFile)) {
                if (!newFile.exists()) {
                    throw new QueueFileException(
                            "Unable to rename " + file + " to " + newFile);
                }
            } else {
                return newFile;
            }
        }
        throw new QueueFileException(RETRY + " attempts to rename file failed!");
    }

    @Override
    public File archiveFile(File file) throws QueueFileException {
        if (!file.exists()) {
            throw new QueueFileException("File " + file + " no longer exists");
        }
        if (!isQueuedFile(file)) {
            throw new QueueFileException("File " + file + " is not in the queue");
        }
        File dest = new File(archiveDirectory, file.getName());
        if (dest.exists()) {
            throw new QueueFileException("Archived file " + dest + " already exists");
        } 
        if (file.renameTo(dest)) {
            LOG.info("File " + file + " archived as " + dest);
            return dest;
        } else {
            throw new QueueFileException("File " + file + " could not be archived - " +
                    "it remains in the queue area");
        }
    }

    @Override
    public void removeFile(File file) throws QueueFileException {
        if (!file.exists()) {
            throw new QueueFileException("File " + file + " no longer exists");
        }
        if (!isQueuedFile(file)) {
            throw new QueueFileException("File " + file + " is not in the queue");
        }
        if (file.delete()) {
            LOG.info("File " + file + " deleted from queue area");
        } else {
            throw new QueueFileException("File " + file + " could not be deleted from queue area");
        }
    }

    @Override
    public boolean isCopiedFile(File file) {
        if (!Files.exists(file.toPath())) {
            LOG.info("File " + file + " does not exist");
            return false;
        } else {
            return file.getParentFile().equals(captureDirectory) ||
                file.getParentFile().equals(archiveDirectory);
        }
    }
}

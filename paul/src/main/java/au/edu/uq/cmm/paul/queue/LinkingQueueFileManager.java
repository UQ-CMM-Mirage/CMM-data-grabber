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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.paul.PaulConfiguration;

/**
 * This queue file manager uses symbolic links where possible for files in the queue.
 * 
 * @author scrawley
 */
public class LinkingQueueFileManager extends AbstractQueueFileManager implements QueueFileManager {
    
    private static final Logger LOG = LoggerFactory.getLogger(LinkingQueueFileManager.class);
    
    public LinkingQueueFileManager(PaulConfiguration config) {
        super(config, LOG);
    }

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation enqueues a file by creating a symbolic link to it.
	 */
    @Override
    public File enqueueFile(File source, String suffix, boolean regrabbing) 
                throws QueueFileException, InterruptedException {
        File target = generateUniqueFile(suffix, regrabbing);
        try {
            Files.createSymbolicLink(target.toPath(), source.toPath(),
                    new FileAttribute<?>[0]);
            LOG.info("Symlinked " + source + " as " + target);
            return target;
        } catch (IOException ex) {
            throw new QueueFileException("Problem while copying file to queue", ex);
        }
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
                if (!Files.exists(newFile.toPath(), LinkOption.NOFOLLOW_LINKS)) {
                    throw new QueueFileException(
                            "Unable to rename symlink " + file + " to " + newFile);
                }
            } else {
                return newFile;
            }
        }
        throw new QueueFileException(RETRY + " attempts to rename symlink failed!");
    }

    @Override
    public File archiveFile(File file) throws QueueFileException {
        if (!Files.exists(file.toPath(), LinkOption.NOFOLLOW_LINKS)) {
            throw new QueueFileException("File or symlink " + file + " no longer exists");
        }
        if (!Files.exists(file.toPath())) {
            throw new QueueFileException("Symlink target for " + file + " no longer exists");
        }
        if (!isQueuedFile(file)) {
            throw new QueueFileException("File or symlink" + file + " is not in the queue");
        }
        File dest = new File(archiveDirectory, file.getName());
        if (dest.exists()) {
            throw new QueueFileException("Archived file " + dest + " already exists");
        }

        if (Files.isSymbolicLink(file.toPath())) {
            dest = copyFile(file, dest, "archive");
            try {
                Files.delete(file.toPath());
            } catch (IOException ex) {
                throw new QueueFileException("Could not remove symlink " + file);
            }
        } else {
            if (!file.renameTo(dest)) {
                throw new QueueFileException("File " + file + " could not be renamed to " + dest);
            }
        }
        LOG.info("File " + file + " archived as " + dest);
        return dest;
    }

    @Override
    public void removeFile(File file) throws QueueFileException {
        if (!Files.exists(file.toPath(), LinkOption.NOFOLLOW_LINKS)) {
            throw new QueueFileException("File or symlink " + file + " no longer exists");
        }
        if (!Files.exists(file.toPath())) {
            throw new QueueFileException("Symlink target for " + file + " no longer exists");
        }
        if (!isQueuedFile(file)) {
            throw new QueueFileException("File or symlink" + file + " is not in the queue");
        }
        try {
            Files.delete(file.toPath());
            LOG.info("File " + file + " deleted from queue area");
        } catch (IOException ex) {
            throw new QueueFileException("File " + file + " could not be deleted from queue area", ex);
        }
    }

    @Override
    public boolean isCopiedFile(File file) throws QueueFileException {
        Path target = file.toPath();
        if (!Files.exists(target)) {
            LOG.info("File " + file + " does not exist");
            return false;
        } else if (Files.isSymbolicLink(target)) {
            try {
                target = Files.readSymbolicLink(target);
            } catch (IOException ex) {
                LOG.info("Symlink " + file + " can't be read: " + ex.getMessage());
                return false;
            }
        }
        return target.getParent().toFile().equals(captureDirectory) ||
                target.getParent().toFile().equals(archiveDirectory);
    }
}

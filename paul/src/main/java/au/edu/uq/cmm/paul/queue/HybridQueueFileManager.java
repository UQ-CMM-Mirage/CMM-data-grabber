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
import java.nio.file.attribute.FileAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.paul.PaulConfiguration;

/**
 * This queue file manager copies or uses symbolic links depending on the file size.
 * 
 * @author scrawley
 */
public class HybridQueueFileManager extends AbstractQueueFileManager implements QueueFileManager {
    
    private static final Logger LOG = LoggerFactory.getLogger(HybridQueueFileManager.class);
    
    private final long fileSizeThreshold;
    
    public HybridQueueFileManager(PaulConfiguration config) {
        super(config, LOG);
        fileSizeThreshold = config.getQueueFileSizeThreshold();
    }

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation enqueues a file by either copying it or creating a symbolic link to it,
	 * depending on the file size.
	 */
    @Override
    public File enqueueFile(File source, String suffix, boolean regrabbing) 
                throws QueueFileException, InterruptedException {
        File target = generateUniqueFile(suffix, regrabbing);
        try {
            if (Files.size(source.toPath()) < fileSizeThreshold) {
                return copyFile(source, target, "queue");
            } else {
                Files.createSymbolicLink(target.toPath(), source.toPath(),
                        new FileAttribute<?>[0]);
                LOG.info("Symlinked " + source + " as " + target);
                return target;
            }
        } catch (IOException ex) {
            throw new QueueFileException("Problem while enqueing file " + source, ex);
        }
    }
}

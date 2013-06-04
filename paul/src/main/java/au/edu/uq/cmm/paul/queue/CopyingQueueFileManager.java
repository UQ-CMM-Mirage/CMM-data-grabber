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
    private static final Logger LOG = LoggerFactory.getLogger(CopyingQueueFileManager.class);
    
    public CopyingQueueFileManager(PaulConfiguration config) {
        super(config, LOG);
    }

	@Override
    public File enqueueFile(File source, String suffix, boolean regrabbing) 
                throws QueueFileException, InterruptedException {
        File target = generateUniqueFile(suffix, regrabbing);
        return copyFile(source, target, "queue");
    }
}

/*
* Copyright 2012, CMM, University of Queensland.
*
* This file is part of AclsLib.
*
* AclsLib is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* AclsLib is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with AclsLib. If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.uq.cmm.paul.queue;

import java.io.File;

public interface QueueFileManager {

    File enqueueFile(File source, String suffix, boolean regrabbing) throws QueueFileException;
    
    void enqueueFile(String contents, File target) throws QueueFileException;
    
    File archiveFile(File queueFile) throws QueueFileException;
    
    void removeFile(File file) throws QueueFileException;
    
    boolean isCopiedFile(File file);
    
    boolean isQueuedFile(File file);
    
    boolean isArchivedFile(File file);

    File generateUniqueFile(String string, boolean regrabbing) throws QueueFileException;

    File renameGrabbedDatafile(File file) throws QueueFileException;
}

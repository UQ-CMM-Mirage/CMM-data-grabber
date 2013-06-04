/*
* Copyright 2012-2013, CMM, University of Queensland.
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
    
    public enum Strategy {
        /** Data files should be copied into the queue area */
        COPY_FILES, 
        
        /** Data files should be symlinked into the queue area */
        LINK_FILES, 
        
        /** 
         * Data files should be copied into the queue area, provided 
         * they meet certain criteria.
         */
        HYBRID
    }

    /**
     * Add a file to the queue area by copying or symlinking.
     * 
     * @param source the file to be added
     * @param suffix the file suffix (used in the queue file / link's pathname)
     * @param regrabbing if true, use a different naming scheme that avoids collision
     *        with regular (grabbed) queue filenames.
     * @return the name of the file in the queue
     * @throws QueueFileException
     * @throws InterruptedException
     */
    File enqueueFile(File source, String suffix, boolean regrabbing) 
            throws QueueFileException, InterruptedException;
    
    /**
     * Enqueue some content (e.g. metadata) using a supplied pathname.
     * 
     * @param contents the content to be enqueued.
     * @param target the queue file pathname
     * @param mayExist if true, don't complain if the file already exists
     * @throws QueueFileException
     * @throws InterruptedException
     */
    void enqueueFile(String contents, File target, boolean mayExist) 
            throws QueueFileException, InterruptedException;
    
    /**
     * Transfer a queue file to the archive area.  If the queue file is a
     * symlink, attempt to copy the file that it points to.
     * 
     * @param queueFile the queue file or symlink
     * @return the pathname of the archived file
     * @throws QueueFileException
     * @throws InterruptedException
     */
    File archiveFile(File queueFile) 
            throws QueueFileException, InterruptedException;
    
    /**
     * Remove a file or symlink from the queue.
     * @param file the file to be removed
     * @throws QueueFileException
     * @throws InterruptedException
     */
    void removeFile(File file) 
            throws QueueFileException, InterruptedException;
    
    /**
     * Test if this is a queue or archive entry, and if there is actually a copy
     * of the file in the queue / archive.
     * 
     * @param file the pathname to test
     * @return true if this is a copied file.
     * @throws QueueFileException
     */
    boolean isCopiedFile(File file) throws QueueFileException;
    
    /**
     * Test if the pathname is in the queue
     * @param file the pathname to test
     * @return true is this a queue entry
     */
    boolean isQueuedFile(File file);
    
    /**
     * Test if the pathname is in the archive area
     * @param file the pathname to test
     * @return true is this an archive entry
     */
    boolean isArchivedFile(File file);

    /**
     * Generate a unique pathname for a queue entry.
     * 
     * @param suffix the pathname suffix
     * @param regrabbing if true, use the "regrab" form of the pathname
     * @return the pathname
     * @throws QueueFileException
     * @throws InterruptedException
     */
    File generateUniqueFile(String suffix, boolean regrabbing) 
            throws QueueFileException, InterruptedException;

    /**
     * This method is used to rename a regrabbed queue entry to use the "normal" 
     * grabbed filename. 
     * 
     * @param file the current pathname
     * @return the renamed pathname
     * @throws QueueFileException
     * @throws InterruptedException
     */
    File renameGrabbedDatafile(File file) 
            throws QueueFileException, InterruptedException;
}

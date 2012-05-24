/*
* Copyright 2012, CMM, University of Queensland.
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

package au.edu.uq.cmm.paul;

/**
 * Configuration details API for a type of datafile to be grabbed
 * from a facility.
 * 
 * @author scrawley
 */
public interface DatafileTemplateConfig {

    /**
     * Get the regexes used to match the data file.
     */
    String getFilePattern();

    /**
     * The data file's notional mimeType
     */
    String getMimeType();

    /**
     * The data file's suffix (as used in the queue)
     */
    String getSuffix();

    /**
     * If true, the datafile is an optional member of the dataset
     */
    boolean isOptional();
    
    /**
     * If non-zero, this is the minimum size that this file has to be before
     * it can be grabbed.
     */
    int getMinimumSize();
}
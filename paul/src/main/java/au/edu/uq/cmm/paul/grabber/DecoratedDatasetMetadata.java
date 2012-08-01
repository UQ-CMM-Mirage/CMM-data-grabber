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
package au.edu.uq.cmm.paul.grabber;

public class DecoratedDatasetMetadata extends DatasetMetadata {
    private final DatasetMetadata inFolder;
    private final boolean isInFolder;
    private final Group group;

    public DecoratedDatasetMetadata(DatasetMetadata dataset, DatasetMetadata inFolder, Group group) {
        super(dataset.getSourceFilePathnameBase(), 
                dataset.getFacilityFilePathnameBase(), 
                dataset.getMetadataFilePathname(),
                dataset.getUserName(), 
                dataset.getFacilityName(), 
                dataset.getFacilityId(), 
                dataset.getAccountName(), 
                dataset.getEmailAddress(),
                dataset.getCaptureTimestamp(), 
                dataset.getSessionUuid(), 
                dataset.getSessionStartTimestamp(), 
                dataset.getDatafiles());
        this.inFolder = inFolder;
        this.isInFolder = dataset == inFolder;
        this.group = group;
        this.setId(dataset.getId());
    }
    
    public boolean isInFolder() {
        return isInFolder;
    }
    
    public boolean isMatched() {
        return inFolder != null && Analyser.matches(inFolder, this);
    }
    
    public boolean isUnmatched() {
        return inFolder == null || !Analyser.matches(inFolder, this);
    }

    public final Group getGroup() {
        return group;
    }
    
}
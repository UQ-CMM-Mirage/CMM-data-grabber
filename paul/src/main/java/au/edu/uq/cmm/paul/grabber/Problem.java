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

import au.edu.uq.cmm.paul.grabber.Analyser.ProblemType;

public class Problem {
    private final DatasetMetadata dataset;
    private final DatafileMetadata datafile;
    private final String details;
    private final ProblemType type;
    
    public Problem(DatasetMetadata dataset, DatafileMetadata datafile, 
            ProblemType type, String details) {
        super();
        this.dataset = dataset;
        this.datafile = datafile;
        this.details = details;
        this.type = type;
    }
    
    public final DatasetMetadata getDataset() {
        return dataset;
    }
    
    public final DatafileMetadata getDatafile() {
        return datafile;
    }
    
    public final String getDetails() {
        return details;
    }

    public final ProblemType getType() {
        return type;
    }
}
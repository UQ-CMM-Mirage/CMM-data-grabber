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

public class Statistics {
    private final int datasetsInFolder;
    private final int datasetsInDatabase;
    private final int groupsWithDuplicatesInDatabase;
    private final int datasetsUnmatchedInFolder;
    private final int groupsUnmatchedInDatabase;
    private final int groupsInDatabase;

    public Statistics(int datasetsInFolder, int datasetsInDatabase, int groupsInDatabase,
            int groupsWithDuplicatesInDatabase, int datasetsUnmatchedInFolder, 
            int groupsUnmatchedInDatabase) {
        super();
        this.datasetsInFolder = datasetsInFolder;
        this.datasetsInDatabase = datasetsInDatabase;
        this.groupsWithDuplicatesInDatabase = groupsWithDuplicatesInDatabase;
        this.datasetsUnmatchedInFolder = datasetsUnmatchedInFolder;
        this.groupsUnmatchedInDatabase = groupsUnmatchedInDatabase;
        this.groupsInDatabase = groupsInDatabase;
    }

    public final int getDatasetsInFolder() {
        return datasetsInFolder;
    }

    public final int getDatasetsInDatabase() {
        return datasetsInDatabase;
    }

    public final int getGroupsWithDuplicatesInDatabase() {
        return groupsWithDuplicatesInDatabase;
    }

    public final int getDatasetsUnmatchedInFolder() {
        return datasetsUnmatchedInFolder;
    }

    public final int getGroupsUnmatchedInDatabase() {
        return groupsUnmatchedInDatabase;
    }

    public final int getGroupsInDatabase() {
        return groupsInDatabase;
    }
}
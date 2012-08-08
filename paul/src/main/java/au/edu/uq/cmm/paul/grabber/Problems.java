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

import java.util.List;

import au.edu.uq.cmm.paul.grabber.Analyser.ProblemType;

public class Problems {
    private final List<Problem> problems;

    public Problems(List<Problem> problem) {
        this.problems = problem;
    }

    public int getNosProblems() {
        return problems.size();
    }

    public final int getIoError() {
        return count(ProblemType.IO_ERROR);
    }

    public final int getFileSize2() {
        return count(ProblemType.FILE_SIZE_2);
    }

    public final int getFileSize() {
        return count(ProblemType.FILE_SIZE);
    }

    public final int getFileHash2() {
        return count(ProblemType.FILE_HASH_2);
    }

    public final int getFileHash() {
        return count(ProblemType.FILE_HASH);
    }

    public final int getFileMissing() {
        return count(ProblemType.FILE_MISSING);
    }

    public final int getMetadataSize() {
        return count(ProblemType.METADATA_SIZE);
    }

    public final int getMetadataMissing() {
        return count(ProblemType.METADATA_MISSING);
    }

    private int count(ProblemType type) {
        int count = 0;
        for (Problem problem : problems) {
            if (problem.getType() == type) {
                count++;
            }
        }
        return count;
    }

    public final List<Problem> getProblems() {
        return problems;
    }
}
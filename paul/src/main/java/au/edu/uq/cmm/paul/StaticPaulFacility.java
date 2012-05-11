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

import java.util.ArrayList;
import java.util.List;

import au.edu.uq.cmm.aclslib.config.StaticFacilityConfig;

public class StaticPaulFacility extends StaticFacilityConfig implements
        GrabberFacilityConfig {
    
    private boolean caseInsensitive;
    private boolean useFileLocks;
    private int fileSettlingTime;
    private List<StaticDatafileTemplateConfig> datafileTemplates =
            new ArrayList<StaticDatafileTemplateConfig>();

    @Override
    public List<? extends DatafileTemplateConfig> getDatafileTemplates() {
        return datafileTemplates;
    }

    @Override
    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    @Override
    public boolean isUseFileLocks() {
        return useFileLocks;
    }

    @Override
    public int getFileSettlingTime() {
        return fileSettlingTime;
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    public void setUseFileLocks(boolean useFileLocks) {
        this.useFileLocks = useFileLocks;
    }

    public void setFileSettlingTime(int fileSettlingTime) {
        this.fileSettlingTime = fileSettlingTime;
    }

    public void setDatafileTemplates(
            List<StaticDatafileTemplateConfig> datafileTemplates) {
        this.datafileTemplates = datafileTemplates;
    }
}

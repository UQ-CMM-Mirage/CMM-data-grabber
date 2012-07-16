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
package au.edu.uq.cmm.paul.tag;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import au.edu.uq.cmm.paul.grabber.Analyser;
import au.edu.uq.cmm.paul.grabber.DatasetMetadata;

public class MissingDatasetListingTag extends SimpleTagSupport {
    
    private boolean fromQueue;
   

    public final boolean isFromQueue() {
        return fromQueue;
    }

    public final void setFromQueue(boolean fromQueue) {
        this.fromQueue = fromQueue;
    }

    @Override
    public void doTag() throws JspException, IOException {
        Analyser analysis = (Analyser) getJspContext()
                .findAttribute("analysis");
        Collection<DatasetMetadata> all = fromQueue ? analysis.getAll()
                .getMissingFromDatabaseTimeOrdered() : analysis.getAll()
                .getMissingFromFolderTimeOrdered();
        Collection<DatasetMetadata> beforeQEnd = fromQueue ? analysis
                .getBeforeQEnd().getMissingFromDatabase() : analysis
                .getBeforeQEnd().getMissingFromFolder();
        Collection<DatasetMetadata> afterQEnd = fromQueue ? analysis
                .getAfterQEnd().getMissingFromDatabase() : analysis
                .getAfterQEnd().getMissingFromFolder();
        Collection<DatasetMetadata> beforeHWM = fromQueue ? analysis
                .getBeforeHWM().getMissingFromDatabase() : analysis
                .getBeforeHWM().getMissingFromFolder();
        Collection<DatasetMetadata> afterHWM = fromQueue ? analysis
                .getAfterHWM().getMissingFromDatabase() : analysis
                .getAfterHWM().getMissingFromFolder();
        for (DatasetMetadata missing : all) {
            StringBuilder sb = new StringBuilder();
            addTimespan(sb, "before HWM", beforeHWM.contains(missing));
            addTimespan(sb, "after HWM", afterHWM.contains(missing));
            addTimespan(sb, "before Queue End", beforeQEnd.contains(missing));
            addTimespan(sb, "before Queue End", afterQEnd.contains(missing));
            getJspContext().setAttribute("timespans", sb.toString());
            getJspContext().setAttribute("missing", missing);
            getJspBody().invoke(null);
        }
    }

    private void addTimespan(StringBuilder sb, String timespan, boolean contains) {
        if (contains) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(timespan);
        }
    }

}

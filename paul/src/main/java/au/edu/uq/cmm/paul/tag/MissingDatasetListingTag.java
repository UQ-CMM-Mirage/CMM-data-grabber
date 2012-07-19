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
import java.util.Collections;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import au.edu.uq.cmm.paul.grabber.Analyser;
import au.edu.uq.cmm.paul.grabber.Analyser.Statistics;
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
        Analyser analysis = (Analyser) getJspContext().findAttribute("analysis");
        Boolean intertidal = (Boolean) getJspContext().findAttribute("intertidal");
        Collection<DatasetMetadata> all = fromQueue ? 
                analysis.getAll().getMissingFromDatabaseTimeOrdered() : 
                analysis.getAll().getMissingFromFolderTimeOrdered();
        Collection<DatasetMetadata> beforeQEnd = chooseTimespanSet(analysis.getBeforeQEnd());
        Collection<DatasetMetadata> afterQEnd = chooseTimespanSet(analysis.getAfterQEnd());
        Collection<DatasetMetadata> beforeLWM = chooseTimespanSet(analysis.getBeforeLWM());
        Collection<DatasetMetadata> afterLWM = chooseTimespanSet(analysis.getAfterLWM());
        Collection<DatasetMetadata> beforeHWM = chooseTimespanSet(analysis.getBeforeHWM());
        Collection<DatasetMetadata> afterHWM = chooseTimespanSet(analysis.getAfterHWM());
        for (DatasetMetadata missing : all) {
            if (intertidal != null && intertidal) {
                Date timestamp = missing.getLastFileTimestamp();
                if (timestamp == null) {
                    continue;
                }
                if (analysis.getLWM() != null &&
                        analysis.getLWM().getTime() > timestamp.getTime()) {
                    continue;
                }
                if (analysis.getHWM() != null &&
                        analysis.getHWM().getTime() < timestamp.getTime()) {
                    continue;
                }
            }
            StringBuilder sb = new StringBuilder();
            addTimespan(sb, "&#x2264; LWM", beforeLWM.contains(missing));
            addTimespan(sb, "&gt; LWM", afterLWM.contains(missing));
            addTimespan(sb, "&#x2264; HWM", beforeHWM.contains(missing));
            addTimespan(sb, "&gt; HWM", afterHWM.contains(missing));
            addTimespan(sb, "&#x2264; QEnd", beforeQEnd.contains(missing));
            addTimespan(sb, "&gt; QEnd", afterQEnd.contains(missing));
            getJspContext().setAttribute("timespans", sb.toString());
            getJspContext().setAttribute("missing", missing);
            getJspBody().invoke(null);
        }
    }
    
    private Collection<DatasetMetadata> chooseTimespanSet(Statistics stats) {
        if (stats == null) {
            return Collections.emptySet();
        } else if (fromQueue) {
            return stats.getMissingFromDatabase();
        } else {
            return stats.getMissingFromFolder();
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

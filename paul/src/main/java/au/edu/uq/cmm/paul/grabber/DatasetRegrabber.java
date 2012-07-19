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

import java.io.File;
import java.io.IOException;
import java.util.Date;

import au.edu.uq.cmm.eccles.FacilitySession;
import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.status.Facility;

/**
 * The file regrabber performs a limited traversal of the directory containing
 * the source files for a dataset, an creates a WorkEntry ready for recopying.
 * 
 * @author scrawley
 */
public class DatasetRegrabber extends AbstractFileGrabber {
    
    private final DatasetMetadata dataset;
    private final File datasetFile;
    private WorkEntry entry;

    public DatasetRegrabber(Paul services, DatasetMetadata dataset) {
        super(services, getFacility(services, dataset));
        this.dataset = dataset;
        this.datasetFile = new File(dataset.getSourceFilePathnameBase());
    }

    private static Facility getFacility(Paul services, DatasetMetadata dataset) {
        return (Facility) services.getFacilityMapper().lookup(
                null, dataset.getFacilityName(), null);
    }

    @Override
    protected void enqueueWorkEntry(WorkEntry entry) {
        LOG.debug("Enqueue work entry called for " + entry.getBaseFile());
        if (entry.getBaseFile().equals(datasetFile)) {
            LOG.debug("Work entry matched");
            this.entry = entry;
        }
    }
    
    void captureWorkEntry() {
        LOG.debug("Capturing regrab workEntry for " + dataset.getSourceFilePathnameBase());
        File dir = new File(dataset.getSourceFilePathnameBase()).getParentFile();
        analyseTree(dir, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public DatasetMetadata getCandidateDataset() {
        captureWorkEntry();
        entry.pretendToGrabFiles();
        FacilitySession session = getServices().getFacilityStatusManager().getSession(dataset.getSessionUuid());
        DatasetMetadata metadata = entry.assembleDatasetMetadata(
                new Date(), session, new File(dataset.getMetadataFilePathname()));
        return metadata;
    }

    public DatasetMetadata regrabDataset(boolean newDataset) throws InterruptedException, IOException {
        captureWorkEntry();
        LOG.debug("Regrabbing dataset for " + dataset.getSourceFilePathnameBase());
        entry.setTimestamp(new Date());
        return entry.grabFiles(!newDataset);
    }
    
    public void commitRegrabbedDataset(DatasetMetadata dataset, boolean newDataset) throws IOException {
        if (!newDataset) {
            entry.commitRegrabbedDataset(dataset);
        }
    }
}

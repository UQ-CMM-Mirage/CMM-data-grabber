/*
* Copyright 2013, CMM, University of Queensland.
*
* This file is part of Paul.
*
* Paul is free software: you can redistribute it and/or modify
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
* along with Paul. If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.uq.cmm.paul.grabber;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import au.edu.uq.cmm.eccles.FacilitySession;
import au.edu.uq.cmm.paul.GrabberFacilityConfig;
import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.PaulConfiguration;
import au.edu.uq.cmm.paul.queue.QueueManager;
import au.edu.uq.cmm.paul.status.DatafileTemplate;
import au.edu.uq.cmm.paul.status.Facility;
import au.edu.uq.cmm.paul.status.FacilityStatus;
import au.edu.uq.cmm.paul.status.FacilityStatusManager;
import au.edu.uq.cmm.paul.watcher.UncPathnameMapper;

public class AnalyserTest {
    private static EntityManagerFactory EMF;
    private static FacilityStatusManager FSM;
    private static Facility FACILITY;
    private static PaulConfiguration CONFIG;
    private static UncPathnameMapper MAPPER;
    
    private static Logger LOG = Logger.getLogger(AnalyserTest.class);

    @BeforeClass
    public static void setup() {
        EMF = Persistence.createEntityManagerFactory("au.edu.uq.cmm.paul");
        FACILITY = buildFacility();
        CONFIG = new PaulConfiguration();
        CONFIG.setCaptureDirectory(prepareDirectory("/tmp/testSafe").toString());
        CONFIG.setArchiveDirectory(prepareDirectory("/tmp/testArchive").toString());
        FSM = buildMockFacilityStatusManager();
        MAPPER = new UncPathnameMapper() {
            public File mapUncPathname(String uncPathname) {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }
    
    private static Facility buildFacility() {
        Facility facility = new Facility();
        facility.setFacilityName("test");
        facility.setFileArrivalMode(GrabberFacilityConfig.FileArrivalMode.DIRECT);
        DatafileTemplate template = new DatafileTemplate();
        template.setSuffix("txt");
        template.setMimeType("text/plain");
        template.setOptional(true);
        template.setMinimumSize(20);
        template.setFilePattern("(.*)\\.txt");
        DatafileTemplate template2 = new DatafileTemplate();
        template2.setSuffix("tox");
        template2.setMimeType("text/plain");
        template2.setOptional(true);
        template2.setMinimumSize(20);
        template2.setFilePattern("(.*)\\.tox");
        facility.setDatafileTemplates(Arrays.asList(new DatafileTemplate[] {
                template, template2
        }));
        return facility;
    }

    @AfterClass
    public static void teardown() {
        removeCaptureDirectory();
    	LOG.debug("closing EMF");
        EMF.close();
    }

    private static void removeCaptureDirectory() {
        // TODO Auto-generated method stub
    }

    private static File prepareDirectory(String pathname) {
        File res = new File(pathname);
        if (!res.mkdir()) {
            if (!res.isDirectory()) {
                throw new RuntimeException("Can't create the test directory " + pathname + "!");
            }
            // clean it
        }
        return res;
    }

    private static FacilityStatusManager buildMockFacilityStatusManager() {
        FacilityStatusManager fsm = EasyMock.createMock(FacilityStatusManager.class);
        FacilityStatus status = new FacilityStatus();
        FacilitySession session = new FacilitySession();
        session.setFacilityName("test");
        session.setUserName("fred");
        session.setAccount("count");
        SessionDetails details = new SessionDetails(session);
        status.setLocalDirectory(new File("/tmp"));
        EasyMock.expect(fsm.getStatus(FACILITY)).andReturn(status).anyTimes();
        EasyMock.expect(fsm.getSession(EasyMock.eq(FACILITY), EasyMock.anyLong())).
        		andReturn(session).anyTimes();
        EasyMock.expect(fsm.getSessionDetails(EasyMock.eq(FACILITY), 
                EasyMock.anyLong(), EasyMock.anyObject(File.class))).
                andReturn(details).anyTimes();
        fsm.advanceHWMTimestamp(EasyMock.eq(FACILITY), EasyMock.anyObject(Date.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(fsm);
        return fsm;
    }

    @Test
    public void testConstructor() {
        new Analyser(buildMockServices(CONFIG), FACILITY);
    }
    
    @Test
    public void testAnalyse() {
        // This is a bit pointless ... unless we actually give it something to analyse.
        Analyser analyser = new Analyser(buildMockServices(CONFIG), FACILITY);
        analyser.analyse(new Date(0), new Date(), null, false);
        assertEquals(0, analyser.getProblems().getProblems().size());
    }

    private Paul buildMockServices(PaulConfiguration config) {
        return buildMockServices(config, EasyMock.createMock(QueueManager.class));
    }
    
    private Paul buildMockServices(PaulConfiguration config, QueueManager qm) {
        Paul services = EasyMock.createMock(Paul.class);
        EasyMock.expect(services.getEntityManagerFactory()).andReturn(EMF).anyTimes();
        EasyMock.expect(services.getFacilityStatusManager()).andReturn(FSM).anyTimes();
        EasyMock.expect(services.getQueueManager()).andReturn(qm).anyTimes();
        EasyMock.expect(services.getConfiguration()).andReturn(config).anyTimes();
        EasyMock.expect(services.getUncNameMapper()).andReturn(MAPPER).anyTimes();
        EasyMock.replay(services);
        return services;
    }
}

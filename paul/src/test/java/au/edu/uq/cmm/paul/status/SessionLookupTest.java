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

package au.edu.uq.cmm.paul.status;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.BeforeClass;
import org.junit.Test;

import au.edu.uq.cmm.eccles.FacilitySession;
import au.edu.uq.cmm.eccles.UserDetails;
import au.edu.uq.cmm.eccles.UserDetailsManager;
import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.grabber.SessionDetails;

public class SessionLookupTest {
    private static EntityManagerFactory EMF;
    private static FacilitySession FS[];
    private static Facility THIS, THAT;
    
    private static Logger LOG = Logger.getLogger(SessionLookupTest.class);
    

    @BeforeClass
    public static void setup() {
    	THIS = new Facility();
    	THIS.setFacilityName("this");
    	THAT = new Facility();
    	THAT.setFacilityName("that");
    	THAT.setUserOperated(false);
        EMF = Persistence.createEntityManagerFactory("au.edu.uq.cmm.paul");
        EntityManager em = EMF.createEntityManager();
        try {
            em.getTransaction().begin();
            for (FacilitySession session : em.createQuery(
                    "From FacilitySession", FacilitySession.class)
                    .getResultList()) {
                em.remove(session);
            }
            FS = new FacilitySession[] {
                    buildSession("jim", "ac1", "this", "jim@nowhere",
                            "2012-01-01T00:00:00", "2012-01-01T01:00:00"),
                    buildSession("jim", "ac1", "this", "jim@nowhere",
                            "2012-01-01T02:00:00", "2012-01-01T03:00:00"),
                    buildSession("jim", "ac1", "this", "jim@nowhere",
                            "2012-01-01T05:00:00", null),
                    buildSession("jim", "ac1", "this", "jim@nowhere",
                            "2012-01-01T07:00:00", "2012-01-01T08:00:00"),
                    buildSession("jim", "ac1", "this", "jim@nowhere",
                            "2012-01-01T09:00:00", null),
                    buildSession("jim", "ac1", "that", "jim@nowhere",
                            "2012-01-01T09:00:00", "2012-01-01T10:00:00"), };
            for (FacilitySession fs : FS) {
                em.persist(fs);
            }
            em.getTransaction().commit();
        } finally {
            emClose(em);
        }
    }

    @Test
    public void testConstructor() {
        new FacilityStatusManager(buildMockServices());
    }
    
    @Test
    public void testGetFacilitySession() {
        FacilityStatusManager fsm = new FacilityStatusManager(buildMockServices());
        assertEquals(FS[0].getSessionUuid(), fsm.getSession(FS[0].getSessionUuid()).getSessionUuid());
    }
    
    @Test
    public void testGetFacilitySessionUnknown() {
        FacilityStatusManager fsm = new FacilityStatusManager(buildMockServices());
        assertNull(fsm.getSession(UUID.randomUUID().toString()));
    }
    
    @Test
    public void testGetFacilitySessionByTimestamp() {
        FacilityStatusManager fsm = new FacilityStatusManager(buildMockServices());
        assertEquals(FS[0].getSessionUuid(), 
                fsm.getSession(THIS, toTime("2012-01-01T00:00:00")).getSessionUuid());
        assertEquals(FS[0].getSessionUuid(), 
                fsm.getSession(THIS, toTime("2012-01-01T01:00:00")).getSessionUuid());
        assertEquals(FS[1].getSessionUuid(), 
                fsm.getSession(THIS, toTime("2012-01-01T02:00:00")).getSessionUuid());
        assertEquals(FS[1].getSessionUuid(), 
                fsm.getSession(THIS, toTime("2012-01-01T03:00:00")).getSessionUuid());
        assertEquals(FS[2].getSessionUuid(), 
                fsm.getSession(THIS, toTime("2012-01-01T05:00:00")).getSessionUuid());
        assertEquals(FS[2].getSessionUuid(), 
                fsm.getSession(THIS, toTime("2012-01-01T06:00:00")).getSessionUuid());
        assertEquals(FS[3].getSessionUuid(), 
                fsm.getSession(THIS, toTime("2012-01-01T07:00:00")).getSessionUuid());
        assertEquals(FS[3].getSessionUuid(), 
                fsm.getSession(THIS, toTime("2012-01-01T08:00:00")).getSessionUuid());
        assertEquals(FS[4].getSessionUuid(), 
                fsm.getSession(THIS, toTime("2012-01-01T09:00:00")).getSessionUuid());
        assertEquals(FS[4].getSessionUuid(), 
                fsm.getSession(THIS, toTime("2012-01-01T10:00:00")).getSessionUuid());
    }
    
    @Test
    public void testGetFacilitySessionByTimestampUnknown() {
        FacilityStatusManager fsm = new FacilityStatusManager(buildMockServices());
        assertNull(fsm.getSession(THAT, toTime("2012-01-01T00:00:00")));
        assertNull(fsm.getSession(THIS, toTime("2011-01-01T00:00:00")));
        assertNull(fsm.getSession(THIS, toTime("2011-01-01T01:30:00")));
    }
    
    @Test
    public void testGetFacilitySessionByTimestampCurrent() {
        FacilityStatusManager fsm = new FacilityStatusManager(buildMockServices());
        assertNull(fsm.getSession(THIS, toTime("2012-01-01T09:00:00")).getLogoutTime());
    }
    
    @Test
    public void testGetSessionDetails() {
        FacilityStatusManager fsm = new FacilityStatusManager(buildMockServices());
        SessionDetails sd = fsm.getSessionDetails(
                    THAT, toTime("2012-01-01T09:30:00"), new File("/"));
        assertEquals(toTime("2012-01-01T09:00:00"), sd.getLoginTime().getTime());
        assertEquals("jim", sd.getUserName());
        assertEquals(null, sd.getOperatorName());
        assertEquals("ac1", sd.getAccount());
        assertEquals("jim@nowhere", sd.getEmailAddress());
        assertNotNull(sd.getSessionUuid() != null);
        assertEquals("that", sd.getFacilityName());
        
        sd = fsm.getSessionDetails(
                THAT, toTime("2012-01-01T09:30:00"), new File("/bert"));
        assertEquals("bert", sd.getUserName());
        assertEquals("jim", sd.getOperatorName());
        assertEquals("ac1", sd.getAccount());
        assertEquals("bert@nowhere", sd.getEmailAddress());
        assertNotNull(sd.getSessionUuid() != null);
        assertEquals("that", sd.getFacilityName());
        
        sd = fsm.getSessionDetails(
                THAT, toTime("2012-01-01T09:30:00"), new File("/jim"));
        assertEquals("jim", sd.getUserName());
        assertEquals(null, sd.getOperatorName());
        assertEquals("ac1", sd.getAccount());
        assertEquals("jim@nowhere", sd.getEmailAddress());
        assertNotNull(sd.getSessionUuid() != null);
        assertEquals("that", sd.getFacilityName());
    }
    
    private Paul buildMockServices() {
        Paul services = EasyMock.createMock(Paul.class);
        EasyMock.expect(services.getEntityManagerFactory()).andReturn(EMF);
        EasyMock.expect(services.getAclsHelper()).andReturn(null);
        EasyMock.expect(services.getUserDetailsManager()).andReturn(buildMockUserDetailsManager());
        EasyMock.replay(services);
        return services;
    }
    
    private UserDetailsManager buildMockUserDetailsManager() {
        UserDetails bert = new UserDetails();
        bert.setUserName("bert");
        bert.setEmailAddress("bert@nowhere");
        UserDetails jim = new UserDetails();
        jim.setUserName("jim");
        return new MockUserDetailsManager(new UserDetails[]{bert, jim});
    }

    private static FacilitySession buildSession(String name, String account, String facility,
            String email, String login, String logout) {
        FacilitySession session = new FacilitySession();
        session.setAccount(account);
        session.setEmailAddress(email);
        session.setFacilityName(facility);
        session.setUserName(name);
        session.setLoginTime(toDate(login));
        if (logout != null) {
            session.setLogoutTime(toDate(logout));
        }
        session.setSessionUuid(UUID.randomUUID().toString());
        return session;
    }

    private static Date toDate(String date) {
        return ISODateTimeFormat.dateHourMinuteSecond().parseDateTime(date).toDate();
    }

    private static long toTime(String date) {
        return ISODateTimeFormat.dateHourMinuteSecond().parseDateTime(date).getMillis();
    }
    
    private static void emClose(EntityManager em) {
        EntityTransaction t = em.getTransaction();
        if (t.isActive()) {
            LOG.error("Rolling back a stale transaction!!");
            t.rollback();
        }
        em.close();
    }
}

/*
* Copyright 2012, CMM, University of Queensland.
*
* This file is part of AclsLib.
*
* AclsLib is free software: you can redistribute it and/or modify
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
* along with AclsLib. If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.uq.cmm.paul.status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


import org.easymock.EasyMock;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.BeforeClass;
import org.junit.Test;

import au.edu.uq.cmm.eccles.FacilitySession;
import au.edu.uq.cmm.paul.Paul;

public class SessionLookupTest {
    private static EntityManagerFactory EMF;
    private static FacilitySession FS1, FS2;

    @BeforeClass
    public static void setup() {
        EMF = Persistence.createEntityManagerFactory("au.edu.uq.cmm.paul");
        EntityManager em = EMF.createEntityManager();
        try {
            em.getTransaction().begin();
            for (FacilitySession session :
                em.createQuery("From FacilitySession", FacilitySession.class).getResultList()) {
                em.remove(session);
            }
            FS1 = buildSession(
                    "jim", "ac1", "this", "jim@nowhere", "2012-01-01T00:00:00", "2012-01-01T01:00:00");
            FS2 = buildSession(
                    "jim", "ac1", "this", "jim@nowhere", "2012-01-01T02:00:00", "2012-01-01T03:00:00");
            em.persist(FS1);
            em.persist(FS2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    private static FacilitySession buildSession(String name, String account, String facility,
            String email, String login, String logout) {
        FacilitySession session = new FacilitySession();
        session.setAccount(account);
        session.setEmailAddress(email);
        session.setFacilityName(facility);
        session.setUserName(name);
        session.setLoginTime(toDate(login));
        session.setLogoutTime(toDate(logout));
        session.setSessionUuid(UUID.randomUUID().toString());
        return session;
    }

    private static Date toDate(String date) {
        return ISODateTimeFormat.dateHourMinuteSecond().parseDateTime(date).toDate();
    }

    @Test
    public void testConstructor() {
        new FacilityStatusManager(buildMockServices());
    }
    
    @Test
    public void testGetFacilitySession() {
        FacilityStatusManager fsm = new FacilityStatusManager(buildMockServices());
        assertEquals(FS1.getSessionUuid(), fsm.getSession(FS1.getSessionUuid()).getSessionUuid());
    }
    
    @Test
    public void testGetFacilitySessionUnknown() {
        FacilityStatusManager fsm = new FacilityStatusManager(buildMockServices());
        assertEquals(null, fsm.getSession(UUID.randomUUID().toString()));
    }

    private Paul buildMockServices() {
        Paul services = EasyMock.createMock(Paul.class);
        EasyMock.expect(services.getEntityManagerFactory()).andReturn(EMF);
        EasyMock.expect(services.getAclsHelper()).andReturn(null);
        EasyMock.replay(services);
        return services;
    }
}

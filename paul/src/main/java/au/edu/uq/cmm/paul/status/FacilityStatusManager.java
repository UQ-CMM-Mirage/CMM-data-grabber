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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.aclslib.proxy.AclsAuthenticationException;
import au.edu.uq.cmm.aclslib.proxy.AclsHelper;
import au.edu.uq.cmm.aclslib.proxy.AclsInUseException;
import au.edu.uq.cmm.eccles.FacilitySession;
import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.PaulException;
import au.edu.uq.cmm.paul.grabber.FileGrabber;

/**
 * This class represents the session state of the facilities as 
 * captured by the ACLS proxy.
 * 
 * @author scrawley
 */
public class FacilityStatusManager {
    public enum Status {
        ON, DISABLED, OFF
    }

    public static class FacilityStatus {
        private final Long facilityId;
        private Status status;
        private String message;
        private File localDirectory;
        private FileGrabber fileGrabber;
        
        public FacilityStatus(Long facilityId, Status status, String message) {
            super();
            this.facilityId = facilityId;
            this.status = status;
            this.message = message;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Long getFacilityId() {
            return facilityId;
        }

        public FileGrabber getFileGrabber() {
            return this.fileGrabber;
        }

        public void setFileGrabber(FileGrabber fileGrabber) {
            this.fileGrabber = fileGrabber;
        }

        public File getLocalDirectory() {
            return localDirectory;
        }
        
        public void setLocalDirectory(File localDirectory) {
            this.localDirectory = localDirectory;
        }
        
        @Override
        public String toString() {
            return "FacilityStatus [facilityId=" + facilityId + ", status="
                    + status + ", message=" + message + "]";
        }
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(FileGrabber.class);
    private EntityManagerFactory emf;
    private AclsHelper aclsHelper;
    private Map<Long, FacilityStatus> facilityStatuses = 
            new HashMap<Long, FacilityStatus>();

    public FacilityStatusManager(Paul services) {
        this.emf = services.getEntityManagerFactory();
        this.aclsHelper = services.getAclsHelper();
    }

    private Facility getFacility(EntityManager em, String facilityName) {
        TypedQuery<Facility> query = em.createQuery(
                "from Facility f where f.facilityName = :facilityName", Facility.class);
        query.setParameter("facilityName", facilityName);
        List<Facility> res = query.getResultList();
        if (res.size() == 0) {
            return null;
        } else if (res.size() == 1) {
            return res.get(0);
        } else {
            throw new PaulException("Duplicate facility entries");
        }
    }

    public List<FacilitySession> sessionsForFacility(String facilityName) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<FacilitySession> query = em.createQuery(
                    "from FacilitySession s where s.facilityName = :facilityName " +
                    "order by s.loginTime desc", FacilitySession.class);
            query.setParameter("facilityName", facilityName);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public FacilityStatus getStatus(Facility facility) {
        FacilityStatus status = facilityStatuses.get(facility.getId());
        if (status == null) {
            status = new FacilityStatus(facility.getId(), 
                    facility.isDisabled() ? Status.DISABLED : Status.OFF, "");
            facilityStatuses.put(facility.getId(), status);
        }
        return status;
    }

    public void attachStatus(Facility facility) {
        FacilityStatus status = getStatus(facility);
        if (status.getStatus() == Status.OFF && facility.isDisabled()) {
            status.setStatus(Status.DISABLED);
        } else if (status.getStatus() == Status.DISABLED && !facility.isDisabled()) {
            status.setStatus(Status.OFF);
        }
        facility.setStatus(status);
    }

    public List<FacilitySession> getLatestSessions() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<String> query0 = em.createQuery(
                    "select f.facilityName from Facility f order by f.facilityName", String.class);
            List<String> facilityNames = query0.getResultList();
            List<FacilitySession> sessions = new ArrayList<FacilitySession>(facilityNames.size());
            for (String facilityName : facilityNames) {
                FacilitySession session = latestSession(em, facilityName);
                if (session == null) {
                    session = new FacilitySession(facilityName);
                }
                sessions.add(session);
            }
            em.getTransaction().rollback();
            return sessions;
        } finally {
            em.close();
        }
    }

    public FacilitySession getLoginDetails(String facilityName, long timestamp) {
        EntityManager em = emf.createEntityManager();
        try {
            // First, we select the sessions that potentially contain this timestamp.
            // These start at or before the timestamp and either end after it, or don't end.
            // We want the last of these ... so sort descending on start time.
            TypedQuery<FacilitySession> query = em.createQuery(
                    "from FacilitySession s where s.facilityName = :facilityName " +
                    "and s.loginTime <= :timestamp " +
                    "and (s.logoutTime is null or s.logoutTime >= :timestamp) " +
                    "order by s.loginTime desc", FacilitySession.class);
            query.setParameter("facilityName", facilityName);
            query.setParameter("timestamp", new Date(timestamp));
            query.setMaxResults(1);
            List<FacilitySession> list = query.getResultList();
            if (list.size() == 0) {
                LOG.debug("No session on '" + facilityName + "' matches timestamp " + timestamp);
                return null;
            }
            FacilitySession session = list.get(0);
            if (session.getLogoutTime() == null) {
                // If we have a session with no definite end, we need to infer an
                // end point from the start of the next session, if any.
                LOG.debug("Inferring session end ...");
                TypedQuery<FacilitySession> query2 = em.createQuery(
                        "from FacilitySession s where s.facilityName = :facilityName " +
                        "and s.loginTime > :timestamp " +
                        "order by s.loginTime asc", FacilitySession.class);
                query2.setParameter("facilityName", facilityName);
                query2.setParameter("timestamp", session.getLoginTime());
                query2.setMaxResults(1);
                list = query2.getResultList();
                if (list.size() > 0) {
                    FacilitySession session2 = list.get(0);
                    if (session2.getLoginTime().getTime() < timestamp) {
                        LOG.debug("No session on '" + facilityName + 
                                "' matches timestamp " + timestamp + " (2)");
                        return null;
                    }
                }
            }
            LOG.debug("Session for timestamp " + timestamp + " is " + session);
            return session;
        } finally {
            em.close();
        }
    }
    
    public void logoutSession(String sessionUuid) throws AclsAuthenticationException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<FacilitySession> query = em.createQuery(
                    "from FacilitySession s where s.sessionUuid = :uuid",
                    FacilitySession.class);
            query.setParameter("uuid", sessionUuid);
            FacilitySession session = query.getSingleResult();
            if (session.getLogoutTime() == null) {
                session.setLogoutTime(new Date());
            }
            aclsHelper.logout(getFacility(em, session.getFacilityName()), session.getUserName(), 
                    session.getAccount());
            em.getTransaction().commit();
        } catch (NoResultException ex) {
            LOG.debug("session doesn't exist", ex);
        } finally {
            em.close();
        }
    }
    
    public void logoutFacility(String facilityName) throws AclsAuthenticationException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            FacilitySession session = latestSession(em, facilityName);
            if (session != null) {
                aclsHelper.logout(getFacility(em, facilityName), 
                        session.getUserName(), session.getAccount());
                em.getTransaction().commit();
            } else {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    }
    
    private FacilitySession latestSession(EntityManager em, String facilityName) {
        TypedQuery<FacilitySession> query = em.createQuery(
                "from FacilitySession s where s.facilityName = :facilityName " +
                "order by s.loginTime desc", FacilitySession.class);
        query.setParameter("facilityName", facilityName);
        query.setMaxResults(1);
        List<FacilitySession> results = query.getResultList();
        return (results.isEmpty()) ? null : results.get(0);
    }

    public List<String> login(String facilityName, String userName, String password) 
    throws AclsAuthenticationException, AclsInUseException {
        Facility facility = lookupIdleFacility(facilityName);
        return aclsHelper.login(facility, userName, password);
    }

    public void selectAccount (String facilityName, String userName, String account) 
    throws AclsAuthenticationException, AclsInUseException {
        Facility facility = lookupIdleFacility(facilityName);
        aclsHelper.selectAccount(facility, userName, account);
    }

    private Facility lookupIdleFacility(String facilityName)
            throws AclsAuthenticationException, AclsInUseException {
        Facility facility;
        EntityManager em = emf.createEntityManager();
        try {
            FacilitySession session = latestSession(em, facilityName);
            if (session != null && session.getLogoutTime() == null) {
                throw new AclsInUseException(facilityName, session.getUserName());
            }
            facility = getFacility(em, facilityName);
            if (facility == null) {
                throw new AclsAuthenticationException("Unknown facility " + facilityName);
            }
            return facility;
        } finally {
            em.close();
        }
    }
}

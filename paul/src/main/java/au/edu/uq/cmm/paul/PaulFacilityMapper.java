/*
* Copyright 2012-2013, CMM, University of Queensland.
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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import au.edu.uq.cmm.aclslib.config.FacilityConfig;
import au.edu.uq.cmm.aclslib.config.FacilityMapper;
import au.edu.uq.cmm.paul.status.Facility;

public class PaulFacilityMapper implements FacilityMapper {
    
    private EntityManagerFactory entityManagerFactory;
    

    public PaulFacilityMapper(EntityManagerFactory entityManagerFactory) {
        super();
        this.entityManagerFactory = Objects.requireNonNull(entityManagerFactory);
    }

    @Override
    public FacilityConfig lookup(String localHostId, String facilityName,
            InetAddress clientAddr) {
        EntityManager em = entityManagerFactory.createEntityManager();
        TypedQuery<Facility> query;
        Facility res;
        try {
            if (localHostId != null) {
                query = em.createQuery(
                        "from Facility f where f.localHostId = :localHostId",
                        Facility.class);
                query.setParameter("localHostId", localHostId);
                res = getFirst(query, "hostId", localHostId);
                if (res != null) {
                    return res;
                }
            }
            if (facilityName != null) {
                query = em.createQuery(
                        "from Facility f where f.facilityName = :facilityName",
                        Facility.class);
                query.setParameter("facilityName", facilityName);
                res = getFirst(query, "facilityName", facilityName);
                if (res != null) {
                    return res;
                }
            }
            if (clientAddr != null) {
                String ipAddress = clientAddr.getHostAddress();
                String fqdn = clientAddr.getCanonicalHostName();
                String[] hostNameParts = clientAddr.getCanonicalHostName().split("\\.");
                query = em.createQuery(
                        "from Facility f where f.address = :ipAddress or " +
                        "f.address = :fqdn or f.address = :hostName",
                        Facility.class);
                query.setParameter("ipAddress", ipAddress);
                query.setParameter("fqdn", fqdn);
                query.setParameter("hostName", hostNameParts[hostNameParts.length - 1]);
                res = getFirst(query, "ipAddress/hostname", ipAddress + "/" + fqdn);
                if (res != null) {
                    return res;
                }
            }
            return null;
        } finally {
            em.close();
        }
    }

    private Facility getFirst(TypedQuery<Facility> query, String key, String keyValue) {
        List<Facility> list = query.getResultList();
        if (list.size() == 0) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            throw new AssertionError(
                    "Multiple facilities have " + key + " equal to " + keyValue);
        }
    }

    @Override
    public Collection<FacilityConfig> allFacilities() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            TypedQuery<Facility> query = em.createQuery(
                    "from Facility", Facility.class);
            return new ArrayList<FacilityConfig>(query.getResultList());
        } finally {
            em.close();
        }
    }
    

    public static int getFacilityCount(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "select count(facility) from Facility facility", Long.class);
            long res = query.getSingleResult();
            return (int) res;
        } finally {
            em.close();
        }
    }
}

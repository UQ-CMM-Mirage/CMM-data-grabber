/*
* Copyright 2012, CMM, University of Queensland.
*
* This file is part of Eccles.
*
* Eccles is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Eccles is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Eccles. If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.uq.cmm.eccles;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import au.edu.uq.cmm.aclslib.config.ACLSProxyConfiguration;
import au.edu.uq.cmm.aclslib.config.ConfigurationException;
import au.edu.uq.cmm.aclslib.config.FacilityConfig;
import au.edu.uq.cmm.aclslib.config.FacilityMapper;

public class EcclesFacilityMapper implements FacilityMapper {

    private EntityManagerFactory emf;
    private String dummyFacilityName;
    private String dummyFacilityHostId;
    private EcclesFacility dummyFacility;

    public EcclesFacilityMapper(
            ACLSProxyConfiguration config, EntityManagerFactory emf) {
        super();
        this.emf = emf;
        dummyFacilityName = config.getDummyFacilityName();
        dummyFacilityHostId = config.getDummyFacilityHostId();
        dummyFacility = new EcclesFacility();
        dummyFacility.setFacilityName(dummyFacilityName);
        dummyFacility.setLocalHostId(dummyFacilityHostId);
    }

    @Override
    public FacilityConfig lookup(String localHostId, String facilityName,
            InetAddress clientAddr) throws ConfigurationException {
        EntityManager em = emf.createEntityManager();
        TypedQuery<EcclesFacility> query;
        EcclesFacility res;
        try {
            if (localHostId != null) {
                if (localHostId.equals(dummyFacilityHostId)) {
                    return dummyFacility;
                }
                query = em.createQuery(
                        "from EcclesFacility f where f.localHostId = :localHostId",
                        EcclesFacility.class);
                query.setParameter("localHostId", localHostId);
                res = getFirst(query, "hostId", localHostId);
                if (res != null) {
                    return res;
                }
            }
            if (facilityName != null) {
                if (facilityName.equals(dummyFacilityName)) {
                    return dummyFacility;
                }
                query = em.createQuery(
                        "from EcclesFacility f where f.facilityName = :facilityName",
                        EcclesFacility.class);
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
                        "from EcclesFacility f where f.address = :ipAddress or " +
                        "f.address = :fqdn or f.address = :hostName",
                        EcclesFacility.class);
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

    private EcclesFacility getFirst(TypedQuery<EcclesFacility> query, String key, String keyValue) 
            throws ConfigurationException {
        List<EcclesFacility> list = query.getResultList();
        if (list.size() == 0) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            throw new ConfigurationException(
                    "Multiple facilities have " + key + " equal to " + keyValue);
        }
    }

    @Override
    public Collection<FacilityConfig> allFacilities() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<EcclesFacility> query = em.createQuery(
                    "from EcclesFacility", EcclesFacility.class);
            return new ArrayList<FacilityConfig>(query.getResultList());
        } finally {
            em.close();
        }
    }
}

/*
* Copyright 2013, CMM, University of Queensland.
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

package au.edu.uq.cmm.paul.servlet;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.BeforeClass;
import org.junit.Test;

import au.edu.uq.cmm.aclslib.config.ConfigurationException;
import au.edu.uq.cmm.eccles.FacilitySession;
import au.edu.uq.cmm.paul.StaticPaulConfiguration;
import au.edu.uq.cmm.paul.StaticPaulFacilities;
import au.edu.uq.cmm.paul.status.Facility;

public class ConfigurationManagerTest {
    private static EntityManagerFactory EMF;

    @BeforeClass
    public static void setup() {
        EMF = Persistence.createEntityManagerFactory("au.edu.uq.cmm.paul");
        EntityManager em = EMF.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<FacilitySession> query = em.createQuery(
            		"From FacilitySession", FacilitySession.class);
            for (FacilitySession session : query.getResultList()) {
                em.remove(session);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testConstructor() throws ConfigurationException {
        new ConfigurationManager(EMF, buildStaticConfig(),
        		buildStaticFacilities());
    }

	private StaticPaulFacilities buildStaticFacilities() 
			throws ConfigurationException {
		return StaticPaulFacilities.loadFacilities(
				getClass().getResourceAsStream("/test-facilities.json"));
	}

	private StaticPaulConfiguration buildStaticConfig() 
			throws ConfigurationException {
		return StaticPaulConfiguration.loadConfiguration(
				getClass().getResourceAsStream("/test-config.json"));
	}
	
	@Test
	public void testBuildFacility() throws ConfigurationException {
		Facility f = new Facility();
		ConfigurationManager cm = new ConfigurationManager(
					EMF, buildStaticConfig(),
					buildStaticFacilities());
		EntityManager em = EMF.createEntityManager();
		try {
			Map<?, ?> params = buildParamMap(
					"facilityName", "fred", "localHostId", "",
					"address", "127.0.0.1", "accessPassword", "",
					"lastTemplate", "0", "driveName", "",
					"fileSettlingTime", "1000", "folderName", "/foo",
					"accessName", "", "facilityDescription", "");
			assertEquals(cm.buildFacility(f, params, em).toString(), "{}");
			params = buildParamMap(
					"facilityName", "fred", "localHostId", "",
					"address", "127.0.0.1", "accessPassword", "",
					"lastTemplate", "x", "driveName", "",
					"fileSettlingTime", "1000", "folderName", "/foo",
					"accessName", "", "facilityDescription", "");
			assertEquals(cm.buildFacility(f, params, em).toString(), 
					"{lastTemplate=this value is not a valid integer}");
		} finally {
			em.close();
		}
		
		
	}

	private Map<?, ?> buildParamMap(String ... args) {
		Map<String, String[]> res = new HashMap<>();
		for (int i = 0; i < args.length; i += 2) {
			res.put(args[i], new String[]{args[i + 1]});
		}
		return res;
	}
}

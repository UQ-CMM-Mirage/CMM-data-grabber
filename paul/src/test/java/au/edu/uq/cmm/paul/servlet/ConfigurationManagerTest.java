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
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.BeforeClass;
import org.junit.Test;

import au.edu.uq.cmm.aclslib.config.ConfigurationException;
import au.edu.uq.cmm.eccles.FacilitySession;
import au.edu.uq.cmm.paul.GrabberFacilityConfig;
import au.edu.uq.cmm.paul.StaticPaulConfiguration;
import au.edu.uq.cmm.paul.StaticPaulFacilities;
import au.edu.uq.cmm.paul.StaticPaulFacility;
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
	public void testLoadStaticFacilities() throws ConfigurationException {
	    StaticPaulFacilities facilities = buildStaticFacilities();
	    List<StaticPaulFacility> list = facilities.getFacilities();
	    assertEquals(1, list.size());
	    StaticPaulFacility facility = list.get(0);
	    assertEquals(GrabberFacilityConfig.FileArrivalMode.DIRECT, 
	            facility.getFileArrivalMode());
	}
	
	@Test
	public void testBuildFacility1() throws ConfigurationException {
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
			assertEquals("{}", cm.buildFacility(f, params, em).toString());
		} finally {
			em.close();
		}
		
	}
	
	@Test
	public void testBuildFacility2() throws ConfigurationException {
		Facility f = new Facility();
		ConfigurationManager cm = new ConfigurationManager(
					EMF, buildStaticConfig(),
					buildStaticFacilities());
		EntityManager em = EMF.createEntityManager();
		try {
			Map<?, ?> params = buildParamMap(
					"facilityName", "fred", "localHostId", "",
					"address", "127.0.0.1", "accessPassword", "",
					"lastTemplate", "x", "driveName", "",
					"fileSettlingTime", "1000", "folderName", "/foo",
					"accessName", "", "facilityDescription", "");
			assertEquals(
					"{lastTemplate=this value is not a valid integer}",
					cm.buildFacility(f, params, em).toString());
		} finally {
			em.close();
		}
	}
	
	@Test
	public void testBuildFacility3() throws ConfigurationException {
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
					"fileSettlingTime", "zzz", "folderName", "/foo",
					"accessName", "", "facilityDescription", "");
			assertEquals(
					"{fileSettlingTime=this value is not a valid integer}",
					cm.buildFacility(f, params, em).toString());
		} finally {
			em.close();
		}
	}

	@Test
	public void testBuildFacility4() throws ConfigurationException {
		Facility f = new Facility();
		ConfigurationManager cm = new ConfigurationManager(
					EMF, buildStaticConfig(),
					buildStaticFacilities());
		EntityManager em = EMF.createEntityManager();
		try {
			Map<?, ?> params = buildParamMap(
					"facilityName", "", "localHostId", "",
					"address", "127.0.0.1", "accessPassword", "",
					"lastTemplate", "0", "driveName", "",
					"fileSettlingTime", "1000", "folderName", "/foo",
					"accessName", "", "facilityDescription", "");
			assertEquals(
					"{facilityName=this field must not be empty}",
					cm.buildFacility(f, params, em).toString());
		} finally {
			em.close();
		}
	}

	@Test
	public void testBuildFacility5() throws ConfigurationException {
		Facility f = new Facility();
		ConfigurationManager cm = new ConfigurationManager(
					EMF, buildStaticConfig(),
					buildStaticFacilities());
		EntityManager em = EMF.createEntityManager();
		try {
			Map<?, ?> params = buildParamMap(
					"facilityName", "fred", "localHostId", "",
					"address", "", "accessPassword", "",
					"lastTemplate", "0", "driveName", "",
					"fileSettlingTime", "1000", "folderName", "/foo",
					"accessName", "", "facilityDescription", "");
			assertEquals(
					"{localHostId=the local host id must be non-empty if address is empty}",
					cm.buildFacility(f, params, em).toString());
		} finally {
			em.close();
		}
	}

	@Test
	public void testBuildFacility6() throws ConfigurationException {
		Facility f = new Facility();
		ConfigurationManager cm = new ConfigurationManager(
					EMF, buildStaticConfig(),
					buildStaticFacilities());
		EntityManager em = EMF.createEntityManager();
		try {
			Map<?, ?> params = buildParamMap(
					"facilityName", "fred", "localHostId", "1234",
					"address", "", "accessPassword", "",
					"lastTemplate", "0", "driveName", "",
					"fileSettlingTime", "1000", "folderName", "",
					"accessName", "", "facilityDescription", "");
			assertEquals(
					"{folderName=this field must not be empty}",
					cm.buildFacility(f, params, em).toString());
		} finally {
			em.close();
		}
	}

	@Test
	public void testBuildFacility7() throws ConfigurationException {
		Facility f = new Facility();
		ConfigurationManager cm = new ConfigurationManager(
					EMF, buildStaticConfig(),
					buildStaticFacilities());
		EntityManager em = EMF.createEntityManager();
		try {
			Map<?, ?> params = buildParamMap(
					"facilityName", "fred", "localHostId", "1234",
					"address", "", "accessPassword", "",
					"lastTemplate", "0", "driveName", "9",
					"fileSettlingTime", "1000", "folderName", "/foo",
					"accessName", "", "facilityDescription", "");
			assertEquals(
					"{driveName=the drive name must be a single uppercase letter}",
					cm.buildFacility(f, params, em).toString());
		} finally {
			em.close();
		}
	}

	@Test
	public void testBuildFacility8() throws ConfigurationException {
		Facility f = new Facility();
		ConfigurationManager cm = new ConfigurationManager(
					EMF, buildStaticConfig(),
					buildStaticFacilities());
		EntityManager em = EMF.createEntityManager();
		try {
			Map<?, ?> params = buildParamMap(
					"facilityName", "fred", "localHostId", "1234",
					"address", "", "accessPassword", "",
					"lastTemplate", "0", "driveName", "ZZ",
					"fileSettlingTime", "1000", "folderName", "/foo",
					"accessName", "", "facilityDescription", "");
			assertEquals(
					"{driveName=the drive name must be a single uppercase letter}",
					cm.buildFacility(f, params, em).toString());
		} finally {
			em.close();
		}
	}

	@Test
	public void testBuildFacility9() throws ConfigurationException {
		Facility f = new Facility();
		ConfigurationManager cm = new ConfigurationManager(
					EMF, buildStaticConfig(),
					buildStaticFacilities());
		EntityManager em = EMF.createEntityManager();
		try {
			Map<?, ?> params = buildParamMap(
					"facilityName", "fred", "localHostId", "1234",
					"address", "", "accessPassword", "",
					"lastTemplate", "0", "driveName", "Z",
					"fileSettlingTime", "-11000", "folderName", "/foo",
					"accessName", "", "facilityDescription", "");
			assertEquals(
					"{fileSettlingTime=the file setting time cannot be negative}",
					cm.buildFacility(f, params, em).toString());
		} finally {
			em.close();
		}
	}

	@Test
	public void testBuildFacility10() throws ConfigurationException {
		Facility f = new Facility();
		ConfigurationManager cm = new ConfigurationManager(
					EMF, buildStaticConfig(),
					buildStaticFacilities());
		EntityManager em = EMF.createEntityManager();
		try {
			Map<?, ?> params = buildParamMap(
					"facilityName", "fred", "localHostId", "1234",
					"address", "1.2.3.5.6", "accessPassword", "",
					"lastTemplate", "0", "driveName", "Z",
					"fileSettlingTime", "1000", "folderName", "/foo",
					"accessName", "", "facilityDescription", "");
			assertEquals(
					"{address=1.2.3.5.6: Name or service not known}",
					cm.buildFacility(f, params, em).toString());
		} finally {
			em.close();
		}
	}

	@Test
	public void testBuildFacility11() throws ConfigurationException {
		Facility f = new Facility();
		ConfigurationManager cm = new ConfigurationManager(
					EMF, buildStaticConfig(),
					buildStaticFacilities());
		EntityManager em = EMF.createEntityManager();
		try {
			Map<?, ?> params = buildParamMap(
					"facilityName", "fred", "localHostId", "1234",
					"address", "wurzle.example.com", "accessPassword", "",
					"lastTemplate", "0", "driveName", "Z",
					"fileSettlingTime", "1000", "folderName", "/foo",
					"accessName", "", "facilityDescription", "");
			assertEquals(
					"{address=wurzle.example.com: Name or service not known}",
					cm.buildFacility(f, params, em).toString());
		} finally {
			em.close();
		}
	}
	
	@Test
	public void testBuildFacility12() throws ConfigurationException {
		Facility f = new Facility();
		ConfigurationManager cm = new ConfigurationManager(
					EMF, buildStaticConfig(),
					buildStaticFacilities());
		EntityManager em = EMF.createEntityManager();
		ValidationResult<Facility> vr = null;
		try {
			Map<?, ?> params = buildParamMap(
					"facilityName", "fred", "localHostId", "1234",
					"address", "127.0.0.1", "accessPassword", "",
					"lastTemplate", "0", "driveName", "Z",
					"fileSettlingTime", "1000", "folderName", "/foo",
					"accessName", "", "facilityDescription", "");
			vr = cm.createFacility(params);
			assertTrue(vr.getDiags().isEmpty());
			params = buildParamMap(
					"facilityName", "bert", "localHostId", "1234",
					"address", "127.0.0.2", "accessPassword", "",
					"lastTemplate", "0", "driveName", "Z",
					"fileSettlingTime", "1000", "folderName", "/foo",
					"accessName", "", "facilityDescription", "");
			assertEquals(
					"{localHostId=local host id '1234' already assigned to facility 'fred'}",
					cm.buildFacility(f, params, em).toString());
		} finally {
			if (vr.isValid()) {
				cm.deleteFacility(vr.getTarget().getFacilityName());
			}
			em.close();
		}
	}

	@Test
	public void testBuildFacility13() throws ConfigurationException {
		Facility f = new Facility();
		ConfigurationManager cm = new ConfigurationManager(
					EMF, buildStaticConfig(),
					buildStaticFacilities());
		EntityManager em = EMF.createEntityManager();
		ValidationResult<Facility> vr = null;
		try {
			Map<?, ?> params = buildParamMap(
					"facilityName", "fred", "localHostId", "1234",
					"address", "127.0.0.1", "accessPassword", "",
					"lastTemplate", "0", "driveName", "Z",
					"fileSettlingTime", "1000", "folderName", "/foo",
					"accessName", "", "facilityDescription", "");
			vr = cm.createFacility(params);
			assertTrue(vr.getDiags().isEmpty());
			params = buildParamMap(
					"facilityName", "bert", "localHostId", "1235",
					"address", "127.0.0.1", "accessPassword", "",
					"lastTemplate", "0", "driveName", "Z",
					"fileSettlingTime", "1000", "folderName", "/foo",
					"accessName", "", "facilityDescription", "");
			assertEquals(
					"{address=address also used by facility 'fred'.  " +
							"Resolve the address conflict or mark both " +
							"facilities as 'multiplexed'}",
					cm.buildFacility(f, params, em).toString());
		} finally {
			if (vr.isValid()) {
				cm.deleteFacility(vr.getTarget().getFacilityName());
			}
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

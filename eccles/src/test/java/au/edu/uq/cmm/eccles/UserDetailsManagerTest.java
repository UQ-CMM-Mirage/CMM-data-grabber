/*
* Copyright 2013, CMM, University of Queensland.
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.aclslib.authenticator.AclsLoginDetails;
import au.edu.uq.cmm.aclslib.message.Certification;

public class UserDetailsManagerTest {
    private static EntityManagerFactory EMF;
    private static EcclesFacility[] FACILITIES;
    
    private static final Logger LOG = 
    		LoggerFactory.getLogger(UserDetailsManagerTest.class);

    
    @SuppressWarnings("serial")
	@BeforeClass
    public static void setup() {
        FACILITIES = new EcclesFacility[]{
        		buildFacility("test0"), 
        		buildFacility("test1"), 
        		buildFacility("test2")};
        EMF = Persistence.createEntityManagerFactory("au.edu.uq.cmm.paul");
        EntityManager em = EMF.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<UserDetails> query = em.createQuery("from UserDetails", UserDetails.class);
            for (UserDetails ud : query.getResultList()) {
               em.remove(ud);
            }
            em.getTransaction().commit();
            em.getTransaction().begin();
            UserDetails ud = buildUserDetails(
            		"jim", "jim", "jim@nowhere", "Jim Spriggs", "CMMMM");
            ud.setCertifications(new HashMap<String, String>(){{
            	put("test1", Certification.VALID.toString());
            	put("test2", Certification.NONE.toString());
            }});
            em.persist(ud);
            em.persist(buildUserDetails(
            		"neddy", null, "neddy@nowhere", "Neddy Seagoon", "CMMMM"));
            em.getTransaction().commit();
        } finally {
            emClose(em);
        }
    }
    
    private static EcclesFacility buildFacility(String name) {
		EcclesFacility res = new EcclesFacility();
		res.setFacilityName(name);
		return res;
	}

	private static UserDetails buildUserDetails(
    		String userName, String password, String emailAddress,
			String humanReadable, String organization) {
    	UserDetails ud = new UserDetails(userName);
    	ud.setEmailAddress(emailAddress);
    	ud.setHumanReadableName(humanReadable);
    	ud.setOrgName(organization);
    	if (password != null) {
    		ud.setDigest(EcclesUserDetailsManager.createDigest(password, 42));
    		ud.setSeed(42);
    	}
    	return ud;
	}

	@AfterClass
    public static void teardown() {
    	LOG.debug("closing EMF");
    	EMF.close();
    }

    @Test
    public void testConstructor() {
        new EcclesUserDetailsManager(EMF, EcclesFallbackMode.NO_FALLBACK);
    }
    
    @Test
    public void testLookup() throws UserDetailsException {
    	EcclesUserDetailsManager udm = 
    			new EcclesUserDetailsManager(EMF, EcclesFallbackMode.NO_FALLBACK);
    	UserDetails ud = udm.lookupUser("jim", true);
    	assertEquals("jim", ud.getUserName());
    	assertEquals("jim@nowhere", ud.getEmailAddress());
    	assertEquals("CMMMM", ud.getOrgName());
    	assertEquals("Jim Spriggs", ud.getHumanReadableName());
    	assertEquals(Collections.emptySet(), ud.getAccounts());
    	assertEquals(2, ud.getCertifications().size());
    	ud = udm.lookupUser("jim", false);
    	assertEquals("jim", ud.getUserName());
    	assertEquals("jim@nowhere", ud.getEmailAddress());
    	assertEquals("CMMMM", ud.getOrgName());
    	assertEquals("Jim Spriggs", ud.getHumanReadableName());
    	try {
    		ud.getAccounts();
    	} catch (PersistenceException ex) {
    		//
    	}
    	try {
    		ud.getCertifications();
    	} catch (PersistenceException ex) {
    		//
    	}
    	try {
    		ud = udm.lookupUser("nobody", true);
    		fail("No exception");
    	} catch (UserDetailsException ex) {
    		assertEquals("User 'nobody' not found", ex.getMessage());
    	}
    }
    
    @Test
    public void testUsersAndNames() throws UserDetailsException {
    	EcclesUserDetailsManager udm = 
    			new EcclesUserDetailsManager(EMF, EcclesFallbackMode.NO_FALLBACK);
    	List<String> names = udm.getUserNames();
    	assertEquals(2, names.size());
    	assertTrue(names.contains("jim"));
    	assertTrue(names.contains("neddy"));
    	List<UserDetails> users = udm.getUsers();
    	assertEquals(2, users.size());
    }
    
    @Test
    public void testAddAndRemove() throws UserDetailsException {
    	EcclesUserDetailsManager udm = 
    			new EcclesUserDetailsManager(EMF, EcclesFallbackMode.NO_FALLBACK);
    	assertEquals(2, udm.getUserNames().size());
    	udm.addUser(new UserDetails("bert"));
    	assertEquals(3, udm.getUserNames().size());
    	udm.removeUser("bert");
    	assertEquals(2, udm.getUserNames().size());
    }
    
    @Test
    public void testAuthenticate1() {
    	EcclesUserDetailsManager udm = 
    			new EcclesUserDetailsManager(EMF, EcclesFallbackMode.NO_FALLBACK);
        assertNull(udm.authenticate("jim", "jim", FACILITIES[0]));
    }
    
    @Test
    public void testAuthenticate2() {
    	EcclesUserDetailsManager udm = 
    			new EcclesUserDetailsManager(EMF, EcclesFallbackMode.USER_PASSWORD);
        assertNull(udm.authenticate("eric", "eric", FACILITIES[0]));
        assertNull(udm.authenticate("jim", "eric", FACILITIES[0]));
        assertNull(udm.authenticate("neddy", "eric", FACILITIES[0]));
        AclsLoginDetails ud = udm.authenticate("jim", "jim", FACILITIES[0]);
        assertNotNull(ud);
        assertEquals("jim", ud.getUserName());
        assertEquals("Jim Spriggs", ud.getHumanReadableName());
        assertEquals(Certification.VALID, ud.getCertification());
        ud = udm.authenticate("jim", "jim", FACILITIES[1]);
        assertEquals(Certification.VALID, ud.getCertification());
        ud = udm.authenticate("jim", "jim", FACILITIES[2]);
        assertEquals(Certification.NONE, ud.getCertification());
    }
    
    @Test
    public void testAuthenticate3() {
    	EcclesUserDetailsManager udm = 
    			new EcclesUserDetailsManager(EMF, EcclesFallbackMode.USER_PASSWORD_OPTIONAL);
        assertNull(udm.authenticate("eric", "eric", FACILITIES[0]));
        assertNull(udm.authenticate("jim", "eric", FACILITIES[0]));
        assertNotNull(udm.authenticate("jim", "jim", FACILITIES[0]));
        assertNotNull(udm.authenticate("neddy", "eric", FACILITIES[0]));
    }
    
    @Test
    public void testAuthenticate4() {
    	EcclesUserDetailsManager udm = 
    			new EcclesUserDetailsManager(EMF, EcclesFallbackMode.USER_ONLY);
        assertNull(udm.authenticate("eric", "eric", FACILITIES[0]));
        assertNotNull(udm.authenticate("jim", "eric", FACILITIES[0]));
        assertNotNull(udm.authenticate("jim", "jim", FACILITIES[0]));
        assertNotNull(udm.authenticate("neddy", "eric", FACILITIES[0]));
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

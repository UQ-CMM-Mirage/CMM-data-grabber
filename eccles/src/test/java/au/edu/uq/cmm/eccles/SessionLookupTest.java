package au.edu.uq.cmm.eccles;
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


import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.eccles.EcclesUserDetailsManager;
import au.edu.uq.cmm.eccles.FacilitySession;
import au.edu.uq.cmm.eccles.UserDetails;
import au.edu.uq.cmm.eccles.UserDetailsManager;

public class SessionLookupTest {
    private static EntityManagerFactory EMF;
    private static FacilitySession FS[];
    
    private static final Logger LOG = LoggerFactory.getLogger(SessionLookupTest.class);

    

    @BeforeClass
    public static void setup() {
    	EMF = Persistence.createEntityManagerFactory("au.edu.uq.cmm.paul");
        EntityManager em = EMF.createEntityManager();
        try {
            em.getTransaction().begin();
            em.getTransaction().commit();
        } finally {
            emClose(em);
        }
    }
    
    @AfterClass
    public static void teardown() {
    	LOG.debug("closing EMF");
    	EMF.close();
    }

    @Test
    public void testConstructor() {
        
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

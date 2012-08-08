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

package au.edu.uq.cmm.paul;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

/**
 * The persisted state of the Data Grabber master control panel.  Contains
 * things like whether the file watcher is enabled or disabled, whether the
 * atom feed is on or off, and whether or not to perform catchup.
 * 
 * @author scrawley
 */

@Entity
@Table(name = "CONTROL")
public class PaulControl {
    private static long ID = 1;
    
    private Long id;
    private boolean doCatchupOnRestart;
    private boolean fileWatcherEnabled;
    private boolean atomFeedEnabled;
    
    public PaulControl() {
        super();
        id = new Long(ID);
    }
    
    public final boolean isDoCatchupOnRestart() {
        return doCatchupOnRestart;
    }
    
    public final void setDoCatchupOnRestart(boolean doCatchupOnRestart) {
        this.doCatchupOnRestart = doCatchupOnRestart;
    }
    
    public final boolean isFileWatcherEnabled() {
        return fileWatcherEnabled;
    }
    
    public final void setFileWatcherEnabled(boolean fileWatcherEnabled) {
        this.fileWatcherEnabled = fileWatcherEnabled;
    }
    
    public final boolean isAtomFeedEnabled() {
        return atomFeedEnabled;
    }
    
    public final void setAtomFeedEnabled(boolean atomFeedEnabled) {
        this.atomFeedEnabled = atomFeedEnabled;
    }
    
    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public static PaulControl load(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<PaulControl> query = em.createQuery("from PaulControl", PaulControl.class);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            PaulControl res = new PaulControl();
            em.getTransaction().begin();
            em.persist(res);
            em.getTransaction().commit();
            return res;
        } finally {
            emf.close();
        }
    }
    
    public static void save(PaulControl control, EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(control);
            em.getTransaction().commit();
        } finally {
            emf.close();
        }
    }
}

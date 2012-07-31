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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import au.edu.uq.cmm.eccles.FacilitySession;

public class FacilitySessionCache {
    
    private TreeMap<Long, SoftReference<FacilitySession>> cache = 
            new TreeMap<Long, SoftReference<FacilitySession>>();
    private ReferenceQueue<FacilitySession> refQueue = new ReferenceQueue<FacilitySession>();
    
    public synchronized FacilitySession lookup(long time) {
        tidy();
        Map.Entry<Long, SoftReference<FacilitySession>> entry = cache.floorEntry(time);
        if (entry != null) {
            FacilitySession session = entry.getValue().get();
            if (session != null && session.getInferredLogoutTime().getTime() >= time) {
                return session;
            }
        }
        return null;
    }
    
    public synchronized void add(FacilitySession session) {
        tidy();
        SoftReference<FacilitySession> ref = new SoftReference<FacilitySession>(session, refQueue);
        cache.put(session.getLoginTime().getTime(), ref);
    }

    private void tidy() {
        if (refQueue.poll() != null) {
            while (refQueue.poll() != null) {}
            Iterator<Map.Entry<Long, SoftReference<FacilitySession>>> it = cache.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, SoftReference<FacilitySession>> entry = it.next();
                if (entry.getValue().get() == null) {
                    it.remove();
                }
            }
        }
    }
    
    
}

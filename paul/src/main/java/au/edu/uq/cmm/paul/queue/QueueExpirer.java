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

package au.edu.uq.cmm.paul.queue;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.aclslib.service.MonitoredThreadServiceBase;
import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.PaulConfiguration;

/**
 * The expirer is a single thread service that periodically expires
 * old entries from the ingestion queue.
 * 
 * @author scrawley
 */
public class QueueExpirer extends MonitoredThreadServiceBase {
    private static final Logger LOG = LoggerFactory.getLogger(QueueExpirer.class);

    private Paul services;

    public QueueExpirer(Paul services) {
        this.services = services;
    }

    @Override
    public void run() {
        PaulConfiguration config = services.getConfiguration();
        long expiryTime = config.getQueueExpiryTime();
        long expiryInterval = config.getQueueExpiryInterval();
        boolean expireByDeleting = config.isExpireByDeleting();
        try {
            if (expiryInterval <= 0 || expiryTime <= 0) {
                LOG.info("Automatic queue expiration is disabled");
                Object lock = new Object();
                synchronized (lock) {
                    lock.wait();
                }
            } else {
                while (true) {
                    LOG.info("Running automatic queue expiration");
                    doExpiry(expiryTime, expireByDeleting);
                    LOG.info("Completed automatic queue expiration");
                    Thread.sleep(expiryInterval * 60 * 1000);
                }
            }
        } catch (InterruptedException ex) {
            LOG.info("Interrupted - we're done");
        }
    }

    private void doExpiry(long expiryTime, boolean expireByDeleting) {
        QueueManager queueManager = services.getQueueManager();
        long millis = System.currentTimeMillis() - expiryTime * 60 * 1000;
        Date cutoff = new Date(millis);
        LOG.info("Expiry cutoff date/time is " + cutoff);
        int nosExpired = queueManager.expireAll(
                expireByDeleting, null, QueueManager.Slice.ALL, cutoff);
        LOG.info("Expired " + nosExpired + " queue entries");
    }

}

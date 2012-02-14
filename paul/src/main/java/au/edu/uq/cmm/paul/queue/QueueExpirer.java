package au.edu.uq.cmm.paul.queue;

import java.util.Date;

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
                // sleep for ever ...
                Object lock = new Object();
                synchronized (lock) {
                    lock.wait();
                }
            } else {
                while (true) {
                    doExpiry(expiryTime, expireByDeleting);
                    Thread.sleep(expiryInterval * 60 * 1000);
                }
            }
        } catch (InterruptedException ex) {
            // time to go away ...
        }
    }

    private void doExpiry(long expiryTime, boolean expireByDeleting) {
        QueueManager queueManager = services.getQueueManager();
        long millis = System.currentTimeMillis() - expiryTime * 60 * 1000;
        Date cutoff = new Date(millis);
        queueManager.expire(expireByDeleting, cutoff);
    }

}

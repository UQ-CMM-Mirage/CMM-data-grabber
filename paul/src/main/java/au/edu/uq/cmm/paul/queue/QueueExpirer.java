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
import java.util.Objects;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.aclslib.service.MonitoredThreadServiceBase;
import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.PaulConfiguration;
import au.edu.uq.cmm.paul.queue.QueueManager.Removal;

/**
 * The expirer is a single thread service that periodically expires
 * old entries from the ingestion queue.
 * <p>
 * This class also provides a command-line entry-point method.
 * 
 * @author scrawley
 */
public class QueueExpirer extends MonitoredThreadServiceBase {
    private static final Logger LOG = LoggerFactory.getLogger(QueueExpirer.class);
    
    private PaulConfiguration config;

    private QueueManager queueManager;

    public QueueExpirer(Paul services) {
        this(services.getConfiguration(), services.getQueueManager());
    }

    public QueueExpirer(PaulConfiguration config, QueueManager queueManager) {
        this.config = Objects.requireNonNull(config);
        this.queueManager = Objects.requireNonNull(queueManager);
    }

    @Override
    public void run() {
        long expiryTime = config.getQueueExpiryTime();
        long expiryInterval = config.getQueueExpiryInterval();
        Removal removal = config.isExpireByDeleting() ? Removal.DELETE : Removal.ARCHIVE;
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
                    doExpiry(expiryTime * 60, removal);
                    LOG.info("Completed automatic queue expiration");
                    Thread.sleep(expiryInterval * 60 * 1000);
                }
            }
        } catch (InterruptedException ex) {
            LOG.info("Interrupted - we're done");
        }
    }

    /**
     * Run the expiration.
     * 
     * @param expiryTime the expiry age (in seconds).
     * @param removal the method of removal (or not)
     * @throws InterruptedException
     */
    private int doExpiry(long expiryTime, Removal removal) 
            throws InterruptedException {
        if (expiryTime <= 0) {
            throw new IllegalArgumentException("Non-positive expiry time");
        }
        long millis = System.currentTimeMillis() - expiryTime * 1000;
        Date cutoff = new Date(millis);
        LOG.info("Expiry cutoff date/time is " + cutoff);
        int nosExpired = queueManager.expireAll(
                removal, null, QueueManager.Slice.ALL, cutoff);
        LOG.info("Expired " + nosExpired + " queue entries");
        return nosExpired;
    }

    /**
     * Entry point for running the Paul queue expirer from the command line.  This is
     * intended to be run out of the installed webapp (with appropriate classpath)
     * so that it will pick up the persistence configuration of the deployed service.
     * 
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        int pos = 0;
        boolean delete = true;
        boolean dryRun = false;
        String logLevel = "error";
        while (pos < args.length) {
            if (args[pos].equals("--help")) {
                bail("", 0);
            } else if (args[pos].equals("--delete")) {
                pos++;
                delete = true;
            } else if (args[pos].equals("--archive")) {
                pos++;
                delete = false;
            } else if (args[pos].equals("--dryrun")) {
                pos++;
                dryRun = true;
            } else if (args[pos].equals("--logging")) {
                pos++;
                if (pos >= args.length) {
                    bail("Missing value for --logging", 1);
                }
                logLevel = args[pos++];
            } else {
                break;
            }
        }
        setLogLevel(logLevel);
        if (pos >= args.length) {
            bail("Missing expiry time", 1);
        } else if (args[pos].startsWith("-")) {
            bail("Unrecognised option: " + args[pos], 1);
        } else if (pos < args.length - 1) {
            bail("Too many command arguments", 1);
        }
        
        String timespec = args[pos];
        char unit = 'h';
        if (timespec.length() > 0 && 
                !Character.isDigit(timespec.charAt(timespec.length() - 1))) {
            unit = timespec.charAt(timespec.length() - 1);
            timespec = timespec.substring(0, timespec.length() - 1);
        }
        long expiryTime;
        try {
            expiryTime = Long.parseLong(timespec);
            if (expiryTime <= 0) {
                bail("Invalid timespec " + args[pos] + ": the numeric part must be > 0", 1);
            }
        } catch (NumberFormatException ex) {
            bail("Invalid timespec " + args[pos] + ": the numeric part is not numeric", 1);
            expiryTime = -1; // shouldn't be reachable ...
        }
        switch (unit) {
        case 'd':
            expiryTime *= 60 * 60 * 24;
            break;
        case 'h':
            expiryTime *= 60 * 60;
            break;
        case 'm':
            expiryTime *= 60;
            break;
        case 's':
            break;
        default:
            bail("Invalid timespec " + args[pos] + ": unknown time unit", 1);
        }
        EntityManagerFactory entityManagerFactory = 
                Persistence.createEntityManagerFactory("au.edu.uq.cmm.paul");
        PaulConfiguration config = PaulConfiguration.load(entityManagerFactory);
        QueueManager queueManager = new QueueManager(config, entityManagerFactory);
        QueueExpirer expirer = new QueueExpirer(config, queueManager);
        try {
            Removal removal = dryRun ? Removal.DRY_RUN : 
                delete ? Removal.DELETE : Removal.ARCHIVE;
            int expired = expirer.doExpiry(expiryTime, removal);
            String actionDescription = 
                    (dryRun ? "would have " : "") + (delete ? "deleted " : "archived ");
            System.out.println(
                    "Expiration " + actionDescription +
                    (expired == 1 ? "1 queue entry" : (expired + " queue entries")));
        } catch (InterruptedException ex) {
            // We can ignore this ...
        }
    }
    
    private static void setLogLevel(String logLevel) {
        // FIXME don't hard-wire the logger implementation.
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
        org.apache.log4j.Level level = org.apache.log4j.Level.toLevel(logLevel);
        logger.setLevel(level);
    }

    private static void bail(String message, int rc) {
        if (message.length() > 0) {
            System.err.println(message);
        }
        System.err.println("Usage: paulExpire [ <options> ] <timeSpec> ");
        System.err.println("  <options> are:");
        System.err.println("    --help            - prints this text");
        System.err.println("    --delete          - delete expired entries (default)");
        System.err.println("    --archive         - archive expired entries");
        System.err.println("    --logging <level> - enable logging");
        System.err.println("  <timeSpec> is analogous to the timespec in 'tmpReaper(8)'");
        System.exit(rc);
    }
}

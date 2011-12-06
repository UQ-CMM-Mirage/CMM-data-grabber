package au.edu.uq.cmm.mirage.grabber;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.log4j.Logger;

import au.edu.uq.cmm.aclslib.proxy.AclsProxy;
import au.edu.uq.cmm.aclslib.server.Configuration;
import au.edu.uq.cmm.mirage.status.FacilityStatusManager;

public class Grabber implements Runnable {
    
    private static final Logger LOG = Logger.getLogger(Grabber.class);
    private Configuration config;
    private Thread grabberThread;
    private FacilityStatusManager statusManager;
    private AclsProxy proxy;
    
    public Grabber(Configuration config, AclsProxy proxy,
            FacilityStatusManager statusManager) {
        this.config = config;
        this.statusManager = statusManager;
        this.proxy = proxy;
    }

    public static void main(String[] args) {
        String configFile = null;
        if (args.length > 0) {
            configFile = args[0];
        }
        try {
            Configuration config = Configuration.loadConfiguration(configFile);
            if (config == null) {
                LOG.info("Can't read/load configuration file");
                System.exit(2);
            }
            AclsProxy proxy = new AclsProxy(config);
            FacilityStatusManager statusManager = new FacilityStatusManager(proxy);
            Grabber grabber = new Grabber(config, proxy, statusManager);
            grabber.startup();
            grabber.shutdown();
            LOG.info("Exitting normally");
            System.exit(0);
        } catch (Throwable ex) {
            LOG.error("Unhandled exception", ex);
            System.exit(1);
        }
    }
    
    private void shutdown() {
        LOG.info("Shutting down");
        try {
            grabberThread.interrupt();
            grabberThread.join(5000);
        } catch (InterruptedException ex) {
            LOG.debug(ex);
        }
        proxy.shutdown();
    }

    private void startup() throws UnknownHostException {
        LOG.info("Starting up");
        proxy.startup();
        grabberThread = launch();
        LOG.info("Started");
        try {
            while (true) {
                if (!grabberThread.isAlive()) {
                    LOG.info("Listener thread died");
                    grabberThread.join();
                    grabberThread = launch();
                    LOG.info("Restarted");
                }
                Thread.sleep(5000);
            }
        } catch (InterruptedException ex) {
            LOG.debug(ex);
        }
    }

    private Thread launch() throws UnknownHostException {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable ex) {
                LOG.debug(ex);
            }
        });
        thread.start();
        return thread;
    }
    
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    @Override
    public void run() {
        try {
            FileSystem fs = FileSystems.getDefault();
            WatchService watcher = fs.newWatchService();
            Path dir = Paths.get(new File("/tmp").toURI());
            dir.register(watcher, 
                    StandardWatchEventKinds.ENTRY_CREATE, 
                    StandardWatchEventKinds.ENTRY_MODIFY, 
                    StandardWatchEventKinds.ENTRY_DELETE, 
                    StandardWatchEventKinds.OVERFLOW);
            while (true) {
                WatchKey key = watcher.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        LOG.error("Event overflow!");
                        continue;
                    }
                    WatchEvent<Path> ev = cast(event);
                    Path file = dir.resolve(ev.context());
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.err.println("Created - " + file);
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        System.err.println("Modified - " + file);
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.err.println("Deleted - " + file);
                    }
                }
            }
        } catch (IOException ex) {
            throw new GrabberException("Unexpected IO error", ex);
        } catch (InterruptedException ex) {
            LOG.debug("Interrupted ... we're done");
        }
    }
}

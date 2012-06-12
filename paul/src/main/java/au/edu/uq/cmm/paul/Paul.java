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

import java.io.IOException;

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;

import au.edu.uq.cmm.aclslib.message.AclsClient;
import au.edu.uq.cmm.aclslib.proxy.AclsHelper;
import au.edu.uq.cmm.aclslib.service.CompositeServiceBase;
import au.edu.uq.cmm.aclslib.service.ServiceException;
import au.edu.uq.cmm.eccles.UserDetailsManager;
import au.edu.uq.cmm.paul.GrabberConfiguration.DataGrabberRestartPolicy;
import au.edu.uq.cmm.paul.queue.AtomFeed;
import au.edu.uq.cmm.paul.queue.QueueExpirer;
import au.edu.uq.cmm.paul.queue.QueueManager;
import au.edu.uq.cmm.paul.queue.FeedSwitch;
import au.edu.uq.cmm.paul.servlet.ConfigurationManager;
import au.edu.uq.cmm.paul.status.FacilityStatusManager;
import au.edu.uq.cmm.paul.watcher.FileWatcher;
import au.edu.uq.cmm.paul.watcher.SambaUncPathnameMapper;
import au.edu.uq.cmm.paul.watcher.UncPathnameMapper;

public class Paul extends CompositeServiceBase implements Lifecycle {
    // FIXME - need to do something to hook this class into the servlet lifecycle 
    // so that it gets told when the servlet is being shutdown.
    private static final Logger LOG = LoggerFactory.getLogger(Paul.class);
    private final FacilityStatusManager statusManager;
    private final UncPathnameMapper uncNameMapper;
    private final EntityManagerFactory entityManagerFactory;
    private final QueueManager queueManager;
    private final QueueExpirer queueExpirer;
    private final FileWatcher fileWatcher;
    private final UserDetailsManager userDetailsManager;
    private final ConfigurationManager configManager;
    private final PaulFacilityMapper facilityMapper;
    private final AclsHelper aclsHelper;
    private final AtomFeed atomFeed;
    
    
    public Paul(StaticPaulConfiguration staticConfig,
            StaticPaulFacilities staticFacilities,
            EntityManagerFactory entityManagerFactory,
            FeedSwitch feedSwitch)
    throws IOException {
        this(staticConfig, staticFacilities,
                entityManagerFactory, feedSwitch, new SambaUncPathnameMapper());
    }
    
    public Paul(StaticPaulConfiguration staticConfig,
            StaticPaulFacilities staticFacilities,
            EntityManagerFactory entityManagerFactory,
            FeedSwitch feedSwitch,
            UncPathnameMapper uncNameMapper)
    throws IOException {
        this.entityManagerFactory = entityManagerFactory;
        this.configManager = new ConfigurationManager(entityManagerFactory, staticConfig, staticFacilities);
        this.facilityMapper = new PaulFacilityMapper(entityManagerFactory);
        PaulConfiguration activeConfig = configManager.getActiveConfig();
        this.aclsHelper = new AclsHelper(
                activeConfig.getProxyHost(), activeConfig.getProxyPort(), 
                /* 
                 * Use double the default timeout, because the proxy potentially
                 * has to timeout the downstream ACLS server.  (Hack)
                 */
                AclsClient.ACLS_REQUEST_TIMEOUT * 2, 
                false);
        this.statusManager = new FacilityStatusManager(this);
        this.uncNameMapper = uncNameMapper;
        this.fileWatcher = new FileWatcher(this);
        this.queueManager = new QueueManager(this);
        this.queueExpirer = new QueueExpirer(this);
        this.userDetailsManager = new UserDetailsManager(entityManagerFactory);
        this.atomFeed = new AtomFeed(feedSwitch);
    }
    
    @Override
    protected void doShutdown() throws InterruptedException {
        LOG.info("Shutdown started");
        queueExpirer.shutdown();
        fileWatcher.shutdown();
        LOG.info("Shutdown completed");
    }

    @Override
    protected void doStartup() throws ServiceException {
        LOG.info("Startup started");
        if (getConfiguration().getDataGrabberRestartPolicy() !=
                DataGrabberRestartPolicy.NO_AUTO_START) {
            fileWatcher.startup();
        }
        queueExpirer.startup();
        LOG.info("Startup completed");
    }

    public FacilityStatusManager getFacilityStatusManager() {
        return statusManager;
    }

    public PaulConfiguration getConfiguration() {
        return configManager.getActiveConfig();
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public ConfigurationManager getConfigManager() {
        return configManager;
    }

    public FileWatcher getFileWatcher() {
        return fileWatcher;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public UncPathnameMapper getUncNameMapper() {
        return uncNameMapper;
    }

    public UserDetailsManager getUserDetailsManager() {
        return userDetailsManager;
    }

    public PaulFacilityMapper getFacilityMapper() {
        return facilityMapper;
    }

    public AclsHelper getAclsHelper() {
        return aclsHelper;
    }

    public AtomFeed getAtomFeed() {
        return atomFeed;
    }

    @Override
    public void start() {
        startup();
    }

    @Override
    public void stop() {
        try {
            shutdown();
        } catch (InterruptedException ex) {
            LOG.warn("Shutdown interrupted", ex);
        }
    }

    @Override
    public boolean isRunning() {
        return getState() == State.STARTED;
    }
}

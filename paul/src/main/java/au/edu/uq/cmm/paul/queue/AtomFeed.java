package au.edu.uq.cmm.paul.queue;

import au.edu.uq.cmm.aclslib.service.Service;
import au.edu.uq.cmm.aclslib.service.ServiceException;

public class AtomFeed implements Service {
    
    private FeedSwitch feedSwitch;
    
    public AtomFeed(FeedSwitch feedSwitch) {
        this.feedSwitch = feedSwitch;
    }

    @Override
    public void startup() throws ServiceException, InterruptedException {
        startStartup();
    }

    @Override
    public void shutdown() throws ServiceException, InterruptedException {
        startShutdown();
    }

    @Override
    public void startStartup() throws ServiceException {
        feedSwitch.setFeedEnabled(true);
    }

    @Override
    public void startShutdown() throws ServiceException {
        feedSwitch.setFeedEnabled(false);
    }

    @Override
    public void awaitShutdown() throws InterruptedException {
        return;
    }

    @Override
    public State getState() {
        return feedSwitch.isFeedEnabled() ? State.STARTED : State.STOPPED;
    }
}

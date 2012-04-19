package au.edu.uq.cmm.eccles;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.aclslib.config.ACLSProxyConfiguration;
import au.edu.uq.cmm.aclslib.config.FacilityMapper;
import au.edu.uq.cmm.aclslib.proxy.AclsFacilityEvent;
import au.edu.uq.cmm.aclslib.proxy.AclsFacilityEventListener;
import au.edu.uq.cmm.aclslib.proxy.AclsLoginEvent;
import au.edu.uq.cmm.aclslib.proxy.AclsLogoutEvent;
import au.edu.uq.cmm.aclslib.proxy.AclsProxy;

public class Eccles implements AclsFacilityEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(Eccles.class);
    private AclsProxy proxy;
    private EntityManagerFactory emf;
    private SessionDetailMapper userDetailsMapper;
    
    /**
     * @param args
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException {
        LOG.info("Hallo ... I'm the famous Eccles!");
        new Eccles().run();
    }

    private void run() throws InterruptedException {
        emf = Persistence.createEntityManagerFactory("au.edu.uq.cmm.paul");
        ACLSProxyConfiguration config = EcclesProxyConfiguration.load(emf);
        FacilityMapper mapper = new EcclesFacilityMapper(emf);
        userDetailsMapper = new DefaultSessionDetailsMapper();
        proxy = new AclsProxy(config, mapper);
        proxy.addListener(this);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override public void run() {
                LOG.info("Ok.  I'll be off then");
                proxy.startShutdown();
                try {
                    proxy.awaitShutdown();
                } catch (InterruptedException ex) {
                    LOG.error("Shutdown interrupted ...", ex);
                }
                LOG.info("Bye");
            }
        }));
        proxy.startup();
        
        Object lock = new Object();
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ex) {
                LOG.error("Interrupted ...", ex);
            }
        }
    }

    public void eventOccurred(AclsFacilityEvent event) {
        LOG.debug("Processing event " + event);
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            String facilityName = event.getFacilityName();
            FacilitySession session;
            String userName = userDetailsMapper.mapToUserName(
                    event.getUserName(), event.getAccount());
            String accountName = userDetailsMapper.mapToAccountName(
                    event.getUserName(), event.getAccount());
            String emailAddress = userDetailsMapper.mapToEmailAddress(
                    event.getUserName(), event.getAccount());
            if (event instanceof AclsLoginEvent) {
                session = new FacilitySession(
                        userName, accountName, facilityName, emailAddress, new Date());
                em.persist(session);
            } else if (event instanceof AclsLogoutEvent) {
                TypedQuery<FacilitySession> query = em.createQuery(
                        "from FacilitySession f where f.facilityName = :facilityName " +
                                "order by f.loginTime desc", 
                        FacilitySession.class);
                query.setParameter("facilityName", facilityName);
                query.setMaxResults(1);
                List<FacilitySession> sessions = query.getResultList();
                if (sessions.isEmpty()) {
                    throw new InvalidSessionException(
                            "No sessions for facility " + facilityName);
                }
                session = sessions.get(0);
                if (session.getLogoutTime() != null) {
                    throw new InvalidSessionException(
                            "No current session for facility " + facilityName);
                } else if (!session.getUserName().equals(userName) ||
                        !session.getAccount().equals(accountName)) {
                    throw new InvalidSessionException(
                            "Inconsistent session user or account name for facility " + 
                                    facilityName);
                }
                session.setLogoutTime(new Date());
            } 
            em.getTransaction().commit();
        } catch (InvalidSessionException ex) {
            LOG.error("Bad session information - ignoring login/logout event", ex);
        } finally {
            em.close();
        }
    }
}

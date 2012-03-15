package au.edu.uq.cmm.paul.status;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import au.edu.uq.cmm.aclslib.proxy.AclsFacilityEvent;
import au.edu.uq.cmm.aclslib.proxy.AclsFacilityEventListener;
import au.edu.uq.cmm.paul.Paul;


public class UserDetailsManager implements AclsFacilityEventListener {
    
    private EntityManagerFactory entityManagerFactory;
    private SessionDetailMapper aclsAccountMapper;

    public UserDetailsManager(Paul services) {
        this.entityManagerFactory = services.getEntityManagerFactory();
        this.aclsAccountMapper = services.getSessionDetailMapper();
        if (this.aclsAccountMapper == null) {
            this.aclsAccountMapper = new DefaultSessionDetailsMapper();
        }
        services.getProxy().addListener(this);
    }
    
    public UserDetails lookupUser(String userName) throws UnknownUserException {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            TypedQuery<UserDetails> query = em.createQuery(
                    "from UserDetails u where u.userName = :userName", 
                    UserDetails.class);
            query.setParameter("userName", userName);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            throw new UnknownUserException(userName);
        } finally {
            em.close();
        }
    }
    
    public List<String> getUserNames() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                    "select u.userName from UserDetails u", String.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<UserDetails> getUsers() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            TypedQuery<UserDetails> query = em.createQuery(
                    "from UserDetails u", UserDetails.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void eventOccurred(AclsFacilityEvent event) {
        try {
            String userName = aclsAccountMapper.mapToUserName(
                    event.getUserName(), event.getAccount());
            String emailAddress = aclsAccountMapper.mapToEmailAddress(
                    event.getUserName(), event.getAccount());
            try {
                lookupUser(userName);
            } catch (UnknownUserException e) {
                EntityManager em = entityManagerFactory.createEntityManager();
                try {
                    em.getTransaction().begin();
                    em.persist(new UserDetails(userName, emailAddress, null));
                    em.getTransaction().commit();
                } finally {
                    em.close();
                }
            }
        } catch (InvalidSessionException ex) {
            // Ignore this - it is diagnosed elsewhere
        } 
    }
}

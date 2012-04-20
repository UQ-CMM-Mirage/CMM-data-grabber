package au.edu.uq.cmm.paul.status;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import au.edu.uq.cmm.paul.Paul;


public class UserDetailsManager {
    
    private EntityManagerFactory entityManagerFactory;

    public UserDetailsManager(Paul services) {
        this.entityManagerFactory = services.getEntityManagerFactory();
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
}

package au.edu.uq.cmm.eccles;

/**
 * This SessionDetailMapper provides a null mapping of the tendered user
 * name and account name.  Other (optional) mappings return null.
 * 
 * @author scrawley
 */
public class DefaultSessionDetailsMapper implements SessionDetailMapper {

    @Override
    public String mapToUserName(String userName) {
        return userName;
    }

    @Override
    public String mapToAccount(String accountName) {
        return accountName;
    }

    @Override
    public String mapToEmailAddress(String userName)
            throws InvalidSessionException {
        return null;
    }
}

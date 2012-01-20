package au.edu.uq.cmm.paul.status;

/**
 * This SessionDetailMapper provides a null mapping of the tendered user
 * name and account name.  Other (optional) mappings return null.
 * 
 * @author scrawley
 */
public class DefaultSessionDetailsMapper implements SessionDetailMapper {

    @Override
    public String mapToUserName(String userName, String accountName) {
        return userName;
    }

    @Override
    public String mapToAccountName(String userName, String accountName) {
        return accountName;
    }

    @Override
    public String mapToEmailAddress(String userName, String accountName)
            throws InvalidSessionException {
        return null;
    }
}

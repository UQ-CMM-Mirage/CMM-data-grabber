package au.edu.uq.cmm.eccles;

/**
 * A session details mapper is an extension point that can be used to bridge
 * between the user/account details reported by an instrument (e.g. from ACLS)
 * and details that the repository will recognize 
 * 
 * @author scrawley
 */
public interface SessionDetailMapper {

    /**
     * Map the details to a user name.
     * 
     * @param userName the original user name 
     * @param accountName the original account name 
     * @return the mapped name
     * @throws InvalidSessionException
     */
    String mapToUserName(String userName, String accountName)
            throws InvalidSessionException;
    
    /**
     * Map the details to an account name.
     * 
     * @param userName the original user name 
     * @param accountName the original account name 
     * @return the mapped name
     * @throws InvalidSessionException
     */
    String mapToAccountName(String userName, String accountName)
            throws InvalidSessionException;
    
    /**
     * Map the details to a user address.
     * 
     * @param userName the original user name 
     * @param accountName the original account name 
     * @return the mapped email address or null.
     * @throws InvalidSessionException
     */
    String mapToEmailAddress(String userName, String accountName) 
            throws InvalidSessionException;
}

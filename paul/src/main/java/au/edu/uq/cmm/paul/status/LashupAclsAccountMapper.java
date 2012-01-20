package au.edu.uq.cmm.paul.status;

/**
 * This is a lashup session mapper for use with 
 * the UNSW ACLS test server that has been set up to use email
 * addresses as user names.
 * 
 * @author scrawley
 */
public class LashupAclsAccountMapper implements SessionDetailMapper {

    @Override
    public String mapToUserName(String userName, String accountName) 
            throws InvalidSessionException {
        int pos = userName.indexOf('@');
        if (pos > 0) {
            return userName.substring(0, pos);
        } else {
            return userName;
        }
    }

    @Override
    public String mapToAccountName(String userName, String accountName) {
        return accountName;
    }

    @Override
    public String mapToEmailAddress(String userName, String accountName)
            throws InvalidSessionException {
        if (userName.indexOf('@') > 0) {
            return userName;
        } else {
            return null;
        }
    }
}

package au.edu.uq.cmm.eccles;

/**
 * This exception is thrown when there is a problem with the session information
 * as reported to us.  For example, we may not recognize the user name or account
 * name (assuming that there is a mapping involved).
 * 
 * @author scrawley
 */
public class InvalidSessionException extends Exception {

    private static final long serialVersionUID = 2081979094442804790L;

    public InvalidSessionException(String message) {
        super(message);
    }

}

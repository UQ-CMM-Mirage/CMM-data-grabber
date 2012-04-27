package au.edu.uq.cmm.eccles;

@SuppressWarnings("serial")
public class UnknownUserException extends Exception {

    private String userName;

    public UnknownUserException(String userName) {
        super("Unknown user '" + userName + "'");
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}

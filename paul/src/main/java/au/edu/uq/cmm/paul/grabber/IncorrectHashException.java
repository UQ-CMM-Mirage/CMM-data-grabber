package au.edu.uq.cmm.paul.grabber;

@SuppressWarnings("serial")
public class IncorrectHashException extends Exception {

    private final String actualHash;
    private final String expectedHash;

    public IncorrectHashException(String message, String expectedHash, String actualHash) {
        super(message);
        this.expectedHash = expectedHash;
        this.actualHash = actualHash;
    }

    public String getActualHash() {
        return actualHash;
    }

    public String getExpectedHash() {
        return expectedHash;
    }

}

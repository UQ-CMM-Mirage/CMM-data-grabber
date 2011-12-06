package au.edu.uq.cmm.mirage.grabber;

@SuppressWarnings("serial")
public class GrabberException extends RuntimeException {

    public GrabberException(String msg, Throwable ex) {
        super(msg, ex);
    }

}

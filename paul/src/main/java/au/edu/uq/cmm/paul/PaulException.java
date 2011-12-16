package au.edu.uq.cmm.paul;

@SuppressWarnings("serial")
public class PaulException extends RuntimeException {

    public PaulException(String msg, Throwable ex) {
        super(msg, ex);
    }

    public PaulException(String msg) {
        super(msg);
    }

    public PaulException(Throwable ex) {
        super(ex);
    }

}

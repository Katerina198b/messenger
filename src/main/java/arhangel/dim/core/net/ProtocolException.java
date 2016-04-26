package arhangel.dim.core.net;

/**
 *
 */
public class ProtocolException extends Exception {
    public ProtocolException(String msg) {
        super(msg);
    }

    public ProtocolException(String s, Throwable ex) {
        super(ex);
    }
}

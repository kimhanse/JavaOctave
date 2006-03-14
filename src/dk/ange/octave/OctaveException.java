package dk.ange.octave;

/**
 * @author Kim Hansen
 * 
 * Exception from the Octave interface. Most likely caused by the octave process
 * exiting because of a timeout, out of memory error or programming error.
 * 
 * Can also be caused by an OctaveType constructor when the octave variable has
 * the wrong type. This error might be moved to a subclass called
 * OctaveTypeException.
 */
public class OctaveException extends Exception {

    private static final long serialVersionUID = -6545738819976969964L;

    public OctaveException() {
        super();
    }

    public OctaveException(String message) {
        super(message);
    }

    public OctaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public OctaveException(Throwable cause) {
        super(cause);
    }

}

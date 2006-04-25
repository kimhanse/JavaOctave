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

    private static final long serialVersionUID = 1L;

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

    /**
     * Set to true on exceptions thrown from an Octave object when the reason
     * for the exception is that the object has been asked to destroy its octave
     * process.
     */
    private boolean destroyed = false;

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

}

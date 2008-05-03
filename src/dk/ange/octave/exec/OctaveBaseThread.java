package dk.ange.octave.exec;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import dk.ange.octave.exception.OctaveException;

/**
 * Base class that contains functionality shared between OctaveWriterThread and OctaveReaderThread
 */
abstract class OctaveBaseThread extends Thread {

    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(OctaveBaseThread.class);

    private final CyclicBarrier barrier;

    /**
     * The spacer used to see when the entire output of a command has arrived to the reader
     */
    protected String spacer;

    private OctaveException exception;

    /**
     * Constructor that sets final variables
     * 
     * @param barrier
     */
    protected OctaveBaseThread(final CyclicBarrier barrier) {
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    inLoop();
                } catch (final OctaveException e) {
                    log.debug("Caught OctaveException, stays in loop", e);
                }
            }
        } catch (final Throwable t) {
            log.error("Caught Throwable breaks loop", t);
        }
    }

    private void inLoop() throws InterruptedException, BrokenBarrierException {
        // Wait
        barrier.await();
        exception = null;
        try {
            doStuff();
        } catch (final OctaveException e) {
            log.debug("Caught exception", e);
            exception = e;
            throw e;
        } finally {
            // Reset vars
            setSpacer(null);
            // Release main thread
            barrier.await();
        }
    }

    /**
     * The action implemented by the child class
     */
    protected abstract void doStuff();

    /**
     * @param spacer
     */
    public void setSpacer(final String spacer) {
        if (!(this.spacer == null ^ spacer == null)) {
            throw new IllegalArgumentException("Only one can be null: this.spacer='" + this.spacer + "', spacer='"
                    + spacer + "'");
        }
        this.spacer = spacer;
    }

    /**
     * @return the exception thrown in last action, null if there were no exception
     */
    public OctaveException getException() {
        // TODO should this be synchronized?
        return exception;
    }

}

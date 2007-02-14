package dk.ange.octave;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Reader that passes the reading on to the output from the octave process until the spacer reached, then it returns
 * EOF.
 * 
 * @author Kim Hansen
 */
final class OctaveExecuteReader extends Reader {

    private static final Log log = LogFactory.getLog(OctaveExecuteReader.class);

    private BufferedReader octaveReader;

    private String spacer;

    private OctaveInputThread octaveInputThread;

    private Octave octave;

    private StringBuffer buffer;

    private boolean eof = false;

    /**
     * This reader will read from octaveReader until a single line equal() spacer is read, after that this reader will
     * return eof. When this reader is closed it will join() the octaveInputThread and update the state of octave to
     * NONE.
     * 
     * @param octaveReader
     * @param spacer
     * @param octaveInputThread
     * @param octave
     */
    public OctaveExecuteReader(BufferedReader octaveReader, String spacer, OctaveInputThread octaveInputThread,
            Octave octave) {
        this.octaveReader = octaveReader;
        this.spacer = spacer;
        this.octaveInputThread = octaveInputThread;
        this.octave = octave;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (eof)
            return -1;
        if (buffer == null) {
            String line = octaveReader.readLine();
            if (line == null) {
                throw new IOException("Pipe to octave-process broken");
            }
            if (spacer.equals(line)) {
                eof = true;
                return -1;
            }
            buffer = new StringBuffer(line);
            buffer.append('\n');
        }
        int charsRead = Math.min(buffer.length(), len);
        buffer.getChars(0, charsRead, cbuf, off);
        if (charsRead == buffer.length()) {
            buffer = null;
        } else {
            buffer.delete(0, charsRead);
        }
        return charsRead;
    }

    @Override
    public void close() throws IOException {
        try {
            octaveInputThread.join();
        } catch (InterruptedException e) {
            throw new IOException("InterruptedException: " + e);
        }
        if (read() != -1) {
            throw new IOException("read hasn't finished");
        }
        if (octaveReader.ready()) {
            throw new IOException("octaveReader is ready()");
        }
        try {
            octave.setExecuteState(Octave.ExecuteState.NONE);
        } catch (OctaveException e) {
            throw new IOException("OctaveException: " + e);
        }
        log.debug("Reader closed()");
    }

}

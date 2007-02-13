package dk.ange.octave.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Kim Hansen
 * 
 * A Thread that moves data from an {@link InputStream} to an {@link OutputStream}
 */
public final class IOStreamPipeThread extends Thread {

    private static final Log log = LogFactory.getLog(IOStreamPipeThread.class);

    private static final int BUFFERSIZE = 4 * 1024;

    private final InputStream inputStream;

    private final OutputStream outputStream;

    /**
     * Will create a thread that reads from inputStream and writes to outputStream until inputStream reaches EOF. Then
     * it will close inputStream and finish. Remember to wait() on this thread before outputStream is closed.
     * 
     * @param inputStream
     * @param outputStream
     */
    public IOStreamPipeThread(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        final byte[] b = new byte[BUFFERSIZE];
        while (true) {
            final int len;
            try {
                len = inputStream.read(b);
            } catch (IOException e) {
                log.info("Error when reading from inputStream", e);
                return;
            }
            if (len == -1) // eof
                break;
            try {
                outputStream.write(b, 0, len);
                outputStream.flush();
            } catch (IOException e) {
                log.info("Error when writing to outputStream", e);
                return;
            }
        }
        try {
            inputStream.close();
            // Don't close outputStream, other programs might use it
        } catch (IOException e) {
            log.info("Error when closing inputStream", e);
            return;
        }
        log.debug("IOStreamPipeThread finished without error");
    }

}

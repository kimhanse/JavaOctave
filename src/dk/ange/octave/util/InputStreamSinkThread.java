package dk.ange.octave.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Kim Hansen
 * 
 * A Thread that read data from an {@link InputStream} and throws it away
 */
public final class InputStreamSinkThread extends Thread {

    private static final Log log = LogFactory.getLog(InputStreamSinkThread.class);

    private static final int BUFFERSIZE = 4 * 1024;

    private final InputStream inputStream;

    /**
     * Will create a thread that reads from inputStream and discards the read data until inputStream reaches EOF. Then
     * it will close inputStream and finish. There is no reason to wait() for this thread.
     * 
     * @param inputStream
     */
    public InputStreamSinkThread(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        final byte[] b = new byte[BUFFERSIZE];
        while (true) {
            int len;
            try {
                len = inputStream.read(b);
            } catch (IOException e) {
                log.info("Error when reading from inputStream", e);
                return;
            }
            if (len == -1) // eof
                break;
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            log.info("Error when closing inputStream", e);
            return;
        }
        log.debug("InputStreamSinkThread finished without error");
    }

}

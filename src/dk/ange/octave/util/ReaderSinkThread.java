package dk.ange.octave.util;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Kim Hansen
 * 
 * A Thread that read data from a Reader and throws it away
 */
public class ReaderSinkThread extends Thread {

    private static final Log log = LogFactory.getLog(ReaderSinkThread.class);

    private static final int BUFFERSIZE = 4 * 1024;

    private final Reader reader;

    /**
     * Will create a thread that reads from reader and discards the read data until reader reaches EOF. Then it will
     * close reader and finish. There is no reason to wait() for this thread.
     * 
     * @param reader
     */
    public ReaderSinkThread(Reader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        final char[] b = new char[BUFFERSIZE];
        while (true) {
            int len;
            try {
                len = reader.read(b);
            } catch (IOException e) {
                log.info("Error when reading from reader", e);
                return;
            }
            if (len == -1) // eof
                break;
        }
        try {
            reader.close();
        } catch (IOException e) {
            log.info("Error when closing reader", e);
            return;
        }
        log.debug("ReaderSinkThread finished without error");

    }

}

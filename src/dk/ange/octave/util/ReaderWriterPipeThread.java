package dk.ange.octave.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Kim Hansen
 * 
 * A Thread that moves data from a Reader to a Writer
 */
public class ReaderWriterPipeThread extends Thread {

    private static final Log log = LogFactory.getLog(ReaderWriterPipeThread.class);

    private static final int BUFFERSIZE = 4 * 1024;

    private final Reader reader;

    private final Writer writer;

    /**
     * Will create a thread that reads from reader and writes to write until reader reaches EOF. Then it will close
     * reader and finish. Remember to join() this thread before writer is closed.
     * 
     * @param reader
     * @param writer
     */
    public ReaderWriterPipeThread(final Reader reader, final Writer writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void run() {
        final char[] b = new char[BUFFERSIZE];
        while (true) {
            final int len;
            try {
                len = reader.read(b);
            } catch (final IOException e) {
                log.info("Error when reading from reader", e);
                return;
            }
            if (len == -1) {
                break;
            }
            try {
                writer.write(b, 0, len);
                writer.flush();
            } catch (final IOException e) {
                log.info("Error when writing to writer", e);
                return;
            }
        }
        try {
            reader.close();
            // Don't close writer, other programs might use it
        } catch (final IOException e) {
            log.info("Error when closing reader", e);
            return;
        }
        log.debug("ReaderWriterPipeThread finished without error");
    }

}

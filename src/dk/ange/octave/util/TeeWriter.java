package dk.ange.octave.util;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Executes the actions on a single writer to multiple writers.
 * 
 * If the list of writers in the constructor is empty everything that is written will be discarted.
 * 
 * If there is thrown one or more exception all writers will still be accessed, the exceptions will be logged and the
 * last exception thrown will be passed on outside.
 * 
 * @author Kim Hansen
 */
public class TeeWriter extends Writer {

    private static final Log log = LogFactory.getLog(TeeWriter.class);

    private Writer[] writers;

    /**
     * Create a writer that doesn't do anything.
     */
    public TeeWriter() {
        this.writers = new Writer[0];
    }

    /**
     * Create a single writer that writes to multiple writers.
     * 
     * @param writers
     *            the list of writers that should be written to.
     */
    public TeeWriter(Writer... writers) {
        this.writers = writers;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        IOException ioe = null;
        for (Writer writer : writers) {
            try {
                writer.write(cbuf, off, len);
            } catch (IOException e) {
                log.info("Exception during write()", e);
                ioe = e;
            }
        }
        if (ioe != null) {
            throw ioe;
        }
    }

    @Override
    public void flush() throws IOException {
        IOException ioe = null;
        for (Writer writer : writers) {
            try {
                writer.flush();
            } catch (IOException e) {
                log.info("Exception during flush()", e);
                ioe = e;
            }
        }
        if (ioe != null) {
            throw ioe;
        }
    }

    @Override
    public void close() throws IOException {
        IOException ioe = null;
        for (Writer writer : writers) {
            try {
                writer.close();
            } catch (IOException e) {
                log.info("Exception during close()", e);
                ioe = e;
            }
        }
        if (ioe != null) {
            throw ioe;
        }
    }

}

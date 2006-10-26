package dk.ange.octave.util;

import java.io.IOException;
import java.io.Writer;

/**
 * Executes the action on a single writer to multiple writers.
 * 
 * If the list of writers in the constructor is empty everything that is written
 * will be discarted.
 * 
 * If there is thrown an exception the writers at the end of the list will not
 * get the writes. TODO Handle exceptions better.
 * 
 * @author Kim Hansen
 */
public class TeeWriter extends Writer {

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
        for (Writer writer : writers) {
            writer.write(cbuf, off, len);
        }
    }

    @Override
    public void flush() throws IOException {
        for (Writer writer : writers) {
            writer.flush();
        }
    }

    @Override
    public void close() throws IOException {
        for (Writer writer : writers) {
            writer.close();
        }
    }

}

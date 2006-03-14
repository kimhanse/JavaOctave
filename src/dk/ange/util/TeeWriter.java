package dk.ange.util;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Kim Hansen
 * 
 * Executes the action on a single writer to multible writers.
 * 
 * If the list of writers in the constructor is empty everything that is written
 * will be discarted.
 */
public class TeeWriter extends Writer {

    private Writer[] writers;

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

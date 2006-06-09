package dk.ange.octave.util;

import java.io.IOException;
import java.io.Writer;

/**
 * Will protect a Writer from beeing closed by .close(), usefull for protecting
 * stdout and stderr from beeing closed.
 * 
 * @author Kim Hansen
 */
public class NoCloseWriter extends Writer {

    private Writer writer;

    /**
     * Create a NoCloseWriter that will protect writer.
     * 
     * @param writer
     *            the writer to be protected.
     */
    public NoCloseWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (writer == null)
            return;
        writer.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        if (writer == null)
            return;
        writer.flush();
    }

    /**
     * Flushes the writer and looses the connection to it.
     * 
     * @throws IOException
     *             from the underlying writer.
     */
    @Override
    public void close() throws IOException {
        if (writer == null)
            return;
        writer.flush();
        writer = null;
    }

    /**
     * Really closes the underlying writer.
     * 
     * @throws IOException
     *             from the underlying writer.
     * @throws NullPointerException
     *             if the NoCloseWriter has been closed.
     */
    public void reallyClose() throws IOException {
        writer.close();
    }

}

package dk.ange.octave.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Kim Hansen
 * 
 * A Thread that moves data from a Reader to a Writer
 */
public class ReaderWriterPipeThread extends Thread {

    private static final int BUFFERSIZE = 8192;

    private final Reader reader;

    private final Writer writer;

    /**
     * @param reader
     * @param writer
     */
    public ReaderWriterPipeThread(Reader reader, Writer writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void run() {
        try {
            final char[] cbuf = new char[BUFFERSIZE];
            while (true) {
                final int c = reader.read(cbuf);
                if (c < 0)
                    break;
                writer.write(cbuf, 0, c);
                writer.flush();
            }
            reader.close();
            writer.flush(); // Don't close writer, other programs might use it
        } catch (IOException e1) {
            try {
                writer.write(e1.getMessage());
                writer.flush();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

}

package dk.ange.octave.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Kim Hansen
 * 
 * A Thread that moves data from a Reader to a Writer
 */
public class Pipe extends Thread {

    private static final int BUFFERSIZE = 1024;

    Reader reader;

    Writer writer;

    /**
     * @param reader
     * @param writer
     */
    public Pipe(Reader reader, Writer writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void run() {
        try {
            char[] cbuf = new char[BUFFERSIZE];
            while (true) {
                int c = reader.read(cbuf);
                if (c < 0)
                    break;
                writer.write(cbuf, 0, c);
                writer.flush();
            }
            reader.close();
            writer.flush(); // Don't close writer, other programs might use that
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

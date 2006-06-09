package dk.ange.octave.util;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Kim Hansen
 * 
 * A Thread that read data from a Reader and throws it away
 */
public class ReaderSinkThread extends Thread {

    private static final int BUFFERSIZE = 8192;

    private final Reader reader;

    /**
     * @param reader
     */
    public ReaderSinkThread(Reader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        try {
            final char[] cbuf = new char[BUFFERSIZE];
            while (true) {
                final int c = reader.read(cbuf);
                if (c < 0)
                    break;
            }
            reader.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}

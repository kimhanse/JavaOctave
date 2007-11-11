package dk.ange.octave;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dk.ange.octave.util.StringUtil;

/**
 * @author Kim Hansen
 */
final class OctaveInputThread extends Thread {

    private static final Log log = LogFactory.getLog(OctaveInputThread.class);

    private static final int BUFFERSIZE = 4 * 1024;

    private final Reader inputReader;

    private final Writer octaveWriter;

    private final String spacer;

    private final Octave octave;

    /**
     * @param inputReader
     * @param octaveWriter
     * @param spacer
     * @param octave
     */
    public OctaveInputThread(final Reader inputReader, final Writer octaveWriter, final String spacer,
            final Octave octave) {
        this.inputReader = inputReader;
        this.octaveWriter = octaveWriter;
        this.spacer = spacer;
        this.octave = octave;
    }

    @Override
    public void run() {
        try {
            final char[] cbuf = new char[BUFFERSIZE];
            while (true) {
                final int c = inputReader.read(cbuf);
                if (c < 0) {
                    break;
                }
                if (log.isTraceEnabled()) {
                    log.trace("octaveWriter.write(" + StringUtil.jQuote(cbuf, c) + ", 0, " + c + ")");
                }
                octaveWriter.write(cbuf, 0, c);
                octaveWriter.flush();
            }
            inputReader.close();
            octaveWriter.write("\nprintf(\"%s\\n\", \"" + spacer + "\");\n");
            octaveWriter.flush();
            octave.setExecuteState(Octave.ExecuteState.WRITER_OK);
        } catch (final IOException e) {
            System.err.println("Unexpected IOException in OctaveInputThread");
            e.printStackTrace();
        } catch (final OctaveException octaveException) {
            if (octaveException.isDestroyed()) {
                return;
            }
            System.err.println("Unexpected OctaveException in OctaveInputThread");
            octaveException.printStackTrace();
        }
        log.debug("Thread finished succesfully");
    }

}

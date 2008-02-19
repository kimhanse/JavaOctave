/*
 * Copyright 2007, 2008 Ange Optimization ApS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * @author Kim Hansen
 */
package dk.ange.octave;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dk.ange.octave.exception.OctaveException;
import dk.ange.octave.util.StringUtil;

/**
 * Thread that writes data into the octave process
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

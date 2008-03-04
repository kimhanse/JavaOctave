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

import dk.ange.octave.util.StringUtil;

/**
 * Thread that writes to the octave process
 */
final class InputThread extends Thread {

    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(InputThread.class);

    private static final int BUFFERSIZE = 4 * 1024;

    private final OctaveExec octaveExec;

    private final Writer processWriter;

    private Reader currentInput;

    private String currentSpacer;

    private boolean close = false;

    /**
     * @param octaveExec
     * @param processWriter
     * @return the constructed and started InputThread
     */
    public static InputThread factory(final OctaveExec octaveExec, final Writer processWriter) {
        final InputThread inputThread = new InputThread(octaveExec, processWriter);
        inputThread.setName(Thread.currentThread().getName() + "-InputThread");
        // inputThread.setDaemon(true); // FIXME Let close end the thread
        inputThread.start();
        return inputThread;
    }

    /**
     * @param octaveExec
     * @param processWriter
     */
    private InputThread(final OctaveExec octaveExec, final Writer processWriter) {
        this.octaveExec = octaveExec;
        this.processWriter = processWriter;
    }

    @Override
    public void run() {
        while (true) {
            try {
                synchronized (this) {
                    if (!close && currentInput == null) {
                        this.wait();
                    }
                }
            } catch (final InterruptedException e) {
                log.error("wait() threw InterruptedException", e);
            }
            log.trace("Woke up");
            if (close) {
                break;
            }
            if (currentInput != null) {
                doWrite();
                clearCurrent();
            }
        }
    }

    private synchronized void clearCurrent() {
        if (currentInput == null) {
            throw new IllegalStateException("currentInput == null");
        }
        if (currentSpacer == null) {
            throw new IllegalStateException("currentSpacer == null");
        }
        currentInput = null;
        currentSpacer = null;
    }

    private void doWrite() {
        log.debug("Enter doWrite()");
        try {
            final char[] cbuf = new char[BUFFERSIZE];
            while (true) {
                final int c = currentInput.read(cbuf);
                if (c < 0) {
                    break;
                }
                if (log.isTraceEnabled()) {
                    log.trace("octaveWriter.write(" + StringUtil.jQuote(cbuf, c) + ", 0, " + c + ")");
                }
                processWriter.write(cbuf, 0, c);
                processWriter.flush();
            }
            currentInput.close();
            processWriter.write("\nprintf(\"%s\\n\", \"" + currentSpacer + "\");\n");
            processWriter.flush();
            // what if we sleep here??
            octaveExec.markInputWritten();
        } catch (final IOException e) {
            System.err.println("Unexpected IOException in OctaveInputThread");
            e.printStackTrace();
        }
    }

    /**
     * @param input
     * @param spacer
     */
    public synchronized void startWrite(final Reader input, final String spacer) {
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        if (spacer == null) {
            throw new NullPointerException("spacer == null");
        }
        if (currentInput != null) {
            throw new IllegalStateException("currentInput != null");
        }
        if (currentSpacer != null) {
            throw new IllegalStateException("currentSpacer != null");
        }
        log.trace("Start the InputThread " + this);
        currentInput = input;
        currentSpacer = spacer;
        this.notifyAll();
    }

    /**
     * Ask thread to close
     */
    public synchronized void close() {
        close = true;
        this.notify();
    }

}

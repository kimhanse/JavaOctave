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
package dk.ange.octave.exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dk.ange.octave.exception.OctaveException;
import dk.ange.octave.exception.OctaveIOException;
import dk.ange.octave.exception.OctaveStateException;
import dk.ange.octave.util.InputStreamSinkThread;
import dk.ange.octave.util.NoCloseWriter;
import dk.ange.octave.util.ReaderWriterPipeThread;
import dk.ange.octave.util.TeeWriter;

/**
 * The object connecting to the octave process
 */
public final class OctaveExec {

    private static final Log log = LogFactory.getLog(OctaveExec.class);

    private static final String[] CMD_ARRAY = { "octave", "--no-history", "--no-init-file", "--no-line-editing",
            "--no-site-file", "--silent" };

    private final Process process;

    private final Writer processWriter;

    private final BufferedReader processReader;

    private final CyclicBarrier barrier = new CyclicBarrier(3);

    private final OctaveWriterThread writerThread;

    private final OctaveReaderThread readerThread;

    /*
     * TODO We should wait() on this thread before stderrLog is close()'d
     */
    private final Thread errorStreamThread;

    /**
     * Will start the octave process.
     * 
     * @param stdinLog
     *                This writer will capture all that is written to the octave process via stdin, if null the data
     *                will not be captured.
     * @param stderrLog
     *                This writer will capture all that is written from the octave process on stderr, if null the data
     *                will not be captured.
     * @param octaveProgram
     *                This is the path to the octave program, if it is null the program 'octave' will be assumed to be
     *                in the PATH.
     * @param environment
     *                The environment for the octave process, if null the process will inherit the environment for the
     *                virtual mashine.
     * @param workingDir
     *                This will be the working dir for the octave process, if null the process will inherit the working
     *                dir of the current process.
     */
    public OctaveExec(final Writer stdinLog, final Writer stderrLog, final File octaveProgram,
            final String[] environment, final File workingDir) {
        final String[] cmdArray;
        if (octaveProgram == null) {
            cmdArray = CMD_ARRAY;
        } else {
            cmdArray = CMD_ARRAY.clone();
            cmdArray[0] = octaveProgram.getPath();
        }
        try {
            process = Runtime.getRuntime().exec(cmdArray, environment, workingDir);
        } catch (final IOException e) {
            throw new OctaveIOException(e);
        }
        // Connect stderr
        if (stderrLog == null) {
            new InputStreamSinkThread(process.getErrorStream()).start();
            errorStreamThread = null;
        } else {
            errorStreamThread = new ReaderWriterPipeThread(new InputStreamReader(process.getErrorStream()), stderrLog);
            errorStreamThread.setName(Thread.currentThread().getName() + "-ErrorPipe");
            errorStreamThread.start();
        }
        // Connect stdout
        processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // Connect stdin
        if (stdinLog == null) {
            processWriter = new OutputStreamWriter(process.getOutputStream());
        } else {
            processWriter = new TeeWriter(new NoCloseWriter(stdinLog),
                    new OutputStreamWriter(process.getOutputStream()));
        }
        writerThread = OctaveWriterThread.factory(barrier, processWriter);
        readerThread = OctaveReaderThread.factory(barrier, processReader);
    }

    /*
     * New execute
     */

    private final Random random = new Random();

    private String generateSpacer() {
        return "-=+X+=- Octave.java spacer -=+X+=- " + random.nextLong() + " -=+X+=-";
    }

    /**
     * @param input
     * @param output
     */
    public void eval(final WriteFunctor input, final ReadFunctor output) {
        final String spacer = generateSpacer();
        writerThread.setWriteFunctor(input);
        writerThread.setSpacer(spacer);
        readerThread.setReadFunctor(output);
        readerThread.setSpacer(spacer);
        await(); // Start stuff in the other threads
        await(); // Wait for the other threads to finish
        final OctaveException e1 = writerThread.getException();
        if (e1 != null) {
            throw rethrowException(e1);
        }
        final OctaveException e2 = readerThread.getException();
        if (e2 != null) {
            throw rethrowException(e2);
        }
    }

    private OctaveException rethrowException(final OctaveException inException) {
        final OctaveException outException;
        try {
            outException = inException.getClass().getConstructor(String.class, Throwable.class).newInstance(
                    inException.getMessage(), inException);
        } catch (final Exception e) {
            throw new IllegalStateException("Should not happen", e);
        }
        if (getExecuteState() == ExecuteState.DESTROYED) {
            outException.setDestroyed(true);
        }
        return outException;
    }

    private void await() {
        try {
            barrier.await();
        } catch (final Exception e) {
            throw new IllegalStateException("Problems with barrier");
        }
    }

    /**
     * @param writeFunctor
     * @param output
     */
    public void execute(final WriteFunctor writeFunctor, final Writer output) {
        eval(writeFunctor, new WriterReadFunctor(output));
    }

    private void threadsExit() {
        writerThread.exit();
        readerThread.exit();
    }

    /*
     * Old execute
     */

    /**
     * Close the octave process in an orderly fasion.
     */
    public void close() {
        assert check();
        setExecuteState(ExecuteState.CLOSING);
        try {
            processWriter.write("exit\n");
            processWriter.close();
            final String read = processReader.readLine();
            if (read != null) {
                throw new OctaveIOException("Expected reader to be closed: " + read);
            }
            processReader.close();
        } catch (final IOException e) {
            final OctaveIOException octaveException = new OctaveIOException("reader error", e);
            if (getExecuteState() == ExecuteState.DESTROYED) {
                octaveException.setDestroyed(true);
            }
            System.err.println("getExecuteState() : " + getExecuteState());
            throw octaveException;
        }
        threadsExit();
        setExecuteState(ExecuteState.CLOSED);
    }

    /**
     * Kill the octave process without remorse
     */
    public void destroy() {
        setExecuteState(ExecuteState.DESTROYED);
        process.destroy();
        try {
            processWriter.close();
        } catch (final IOException e) {
            throw new OctaveIOException(e);
        }
        threadsExit();
    }

    /**
     * @return Returns always true, return value is needed in order for this to be used in assert statements. If there
     *         was an error OctaveException would be thrown.
     */
    private boolean check() {
        final ExecuteState executeState2 = getExecuteState();
        if (executeState2 != ExecuteState.NONE) {
            final OctaveStateException octaveException = new OctaveStateException("Failed check(), executeState="
                    + executeState2);
            if (executeState2 == ExecuteState.DESTROYED) {
                octaveException.setDestroyed(true);
            }
            throw octaveException;
        }
        return true;
    }

    @SuppressWarnings("all")
    static enum ExecuteState {
        NONE, BOTH_RUNNING, WRITER_OK, CLOSING, CLOSED, DESTROYED
    }

    private ExecuteState executeState = ExecuteState.NONE;

    ExecuteState getExecuteState() {
        return executeState;
    }

    synchronized void setExecuteState(final ExecuteState executeState) {
        // Throw exception with isDestroyed if state changes from DESTROYED
        if (this.executeState == ExecuteState.DESTROYED) {
            final OctaveStateException octaveException = new OctaveStateException("setExecuteState Error: "
                    + this.executeState + " -> " + executeState);
            octaveException.setDestroyed(true);
            throw octaveException;
        }
        // Accepted transitions:
        // - NONE -> BOTH_RUNNING
        // - BOTH_RUNNING -> WRITER_OK
        // - WRITER_OK -> NONE
        // - NONE -> CLOSING
        // - CLOSING -> CLOSED
        // - * -> DESTROYED
        if (!(this.executeState == ExecuteState.NONE && executeState == ExecuteState.BOTH_RUNNING
                || this.executeState == ExecuteState.BOTH_RUNNING && executeState == ExecuteState.WRITER_OK
                || this.executeState == ExecuteState.WRITER_OK && executeState == ExecuteState.NONE
                || this.executeState == ExecuteState.NONE && executeState == ExecuteState.CLOSING
                || this.executeState == ExecuteState.CLOSING && executeState == ExecuteState.CLOSED || executeState == ExecuteState.DESTROYED)) {
            throw new OctaveStateException("setExecuteState Error: " + this.executeState + " -> " + executeState);
        }
        log.debug("State changed from " + this.executeState + " to " + executeState);
        this.executeState = executeState;
    }

}

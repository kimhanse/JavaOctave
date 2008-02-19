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
package dk.ange.octave;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dk.ange.octave.exception.OctaveIOException;
import dk.ange.octave.exception.OctaveParseException;
import dk.ange.octave.exception.OctaveStateException;
import dk.ange.octave.type.OctaveType;
import dk.ange.octave.util.InputStreamSinkThread;
import dk.ange.octave.util.NoCloseWriter;
import dk.ange.octave.util.ReaderWriterPipeThread;
import dk.ange.octave.util.TeeWriter;

/**
 * @author Kim Hansen
 */
public final class Octave {

    private static final Log log = LogFactory.getLog(Octave.class);

    private static final String[] CMD_ARRAY = { "octave", "--no-history", "--no-init-file", "--no-line-editing",
            "--no-site-file", "--silent" };

    private static final int BUFFERSIZE = 8192;

    private final Process process;

    private final Writer processWriter;

    private final BufferedReader processReader;

    private final Writer stdoutLog;

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
     * @param stdoutLog
     *                This writer will capture all that is written from the octave process on stdout, if null the data
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
    public Octave(final Writer stdinLog, final Writer stdoutLog, final Writer stderrLog, final File octaveProgram,
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
            errorStreamThread.start();
        }
        // Connect stdout
        if (stdoutLog == null) {
            this.stdoutLog = new TeeWriter();
        } else {
            this.stdoutLog = stdoutLog;
        }
        processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // Connect stdin
        if (stdinLog == null) {
            processWriter = new OutputStreamWriter(process.getOutputStream());
        } else {
            processWriter = new TeeWriter(new NoCloseWriter(stdinLog),
                    new OutputStreamWriter(process.getOutputStream()));
        }
        // Setup octave process
        // readSetup();
    }

    @SuppressWarnings("unused")
    private void readSetup() {
        try {
            final InputStreamReader setup = new InputStreamReader(getClass().getResourceAsStream("setup.m"));
            final char[] buffer = new char[4096];
            int len;
            while ((len = setup.read(buffer)) != -1) {
                processWriter.write(buffer, 0, len);
            }
            setup.close();
        } catch (final IOException e) {
            throw new OctaveIOException(e);
        }
    }

    /**
     * Will start the octave process in a standard environment.
     * 
     * @param stdinLog
     *                This writer will capture all that is written to the octave process via stdin, if null the data
     *                will not be captured.
     * @param stdoutLog
     *                This writer will capture all that is written from the octave process on stdout, if null the data
     *                will not be captured.
     * @param stderrLog
     *                This writer will capture all that is written from the octave process on stderr, if null the data
     *                will not be captured.
     */
    public Octave(final Writer stdinLog, final Writer stdoutLog, final Writer stderrLog) {
        this(stdinLog, stdoutLog, stderrLog, null, null, null);
    }

    /**
     * Will start the octave process with its output connected to System.out and System.err.
     */
    public Octave() {
        this(null, new OutputStreamWriter(System.out), new OutputStreamWriter(System.err));
    }

    private final Random random = new Random();

    private String generateSpacer() {
        return "-=+X+=- Octave.java spacer -=+X+=- " + random.nextLong() + " -=+X+=-";
    }

    private boolean isSpacer(final String string) {
        return string.matches("-=\\+X\\+=- Octave\\.java spacer -=\\+X\\+=- .* -=\\+X\\+=-");
    }

    /**
     * @param inputReader
     * @return Returns a Reader that will return the result from the statements that octave gets from the inputReader
     */
    public Reader executeReader(final Reader inputReader) {
        assert check();
        final String spacer = generateSpacer();
        assert isSpacer(spacer);
        final OctaveInputThread octaveInputThread = new OctaveInputThread(inputReader, processWriter, spacer, this);
        final OctaveExecuteReader outputReader = new OctaveExecuteReader(processReader, spacer, octaveInputThread, this);
        setExecuteState(ExecuteState.BOTH_RUNNING);
        octaveInputThread.start();
        return outputReader;
    }

    /**
     * @param inputReader
     * @param echo
     */
    public void execute(final Reader inputReader, final boolean echo) {
        assert check();
        final Reader resultReader = executeReader(inputReader);
        try {
            final char[] cbuf = new char[BUFFERSIZE];
            while (true) {
                final int len = resultReader.read(cbuf);
                if (len == -1) {
                    break;
                }
                if (echo) {
                    stdoutLog.write(cbuf, 0, len);
                    stdoutLog.flush();
                }
            }
            resultReader.close();
        } catch (final IOException e) {
            final OctaveIOException octaveException = new OctaveIOException(e);
            if (getExecuteState() == ExecuteState.DESTROYED) {
                octaveException.setDestroyed(true);
            }
            throw octaveException;
        }
        assert check();
    }

    /**
     * @param reader
     */
    public void execute(final Reader reader) {
        execute(reader, true);
    }

    /**
     * @param cmd
     * @param echo
     */
    public void execute(final String cmd, final boolean echo) {
        execute(new StringReader(cmd), echo);
    }

    /**
     * @param cmd
     */
    public void execute(final String cmd) {
        execute(cmd, true);
    }

    /**
     * Convenience overload
     * 
     * @param name
     * @param value
     */
    public void set(final String name, final OctaveType value) {
        set(Collections.singletonMap(name, value));
    }

    /**
     * @param values
     */
    public void set(final Map<String, OctaveType> values) {
        assert check();
        try {
            final Reader resultReader = executeReader(OctaveWriteHelper.octaveReader(values));
            final char[] cbuf = new char[BUFFERSIZE];
            final int len = resultReader.read(cbuf);
            if (len != -1) {
                final String buffer = new String(cbuf, 0, len);
                throw new OctaveIOException("Unexpected output when setting variable in octave: " + buffer);
            }
            resultReader.close();
        } catch (final IOException e) {
            final OctaveIOException octaveException = new OctaveIOException(e);
            if (getExecuteState() == ExecuteState.DESTROYED) {
                octaveException.setDestroyed(true);
            }
            throw octaveException;
        }
        assert check();
    }

    /**
     * @param name
     * @return Returns a Reader that will return the value of the variable name in the octave-text format
     */
    public BufferedReader get(final String name) {
        assert check();
        final BufferedReader resultReader = new BufferedReader(executeReader(new StringReader("save -text - " + name)));
        try {
            String line = processReader.readLine();
            if (line == null || !line.startsWith("# Created by Octave")) {
                throw new OctaveParseException("Not created by Octave?: '" + line + "'");
            }
            line = processReader.readLine();
            final String token = "# name: ";
            if (!line.startsWith(token)) {
                if (isSpacer(line)) {
                    throw new OctaveParseException("no such variable '" + name + "'");
                } else {
                    throw new OctaveParseException("Expected <" + token + ">, but got <" + line + ">");
                }
            }
            final String readname = line.substring(token.length());
            if (!name.equals(readname)) {
                throw new OctaveParseException("Expected variable named \"" + name + "\" but got one named \""
                        + readname + "\"");
            }
        } catch (final IOException e) {
            final OctaveIOException octaveException = new OctaveIOException(e);
            if (getExecuteState() == ExecuteState.DESTROYED) {
                octaveException.setDestroyed(true);
            }
            throw octaveException;
        }
        return resultReader;
    }

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
        setExecuteState(ExecuteState.CLOSED);
    }

    /**
     * @return Returns always true, return value is needed in order for this to be used in assert statements. If there
     *         was an error OctaveException would be thrown. '
     */
    public boolean check() {
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
    }

    @SuppressWarnings("all")
    static enum ExecuteState {
        NONE, BOTH_RUNNING, WRITER_OK, CLOSING, CLOSED, DESTROYED
    }

    private ExecuteState executeState = ExecuteState.NONE;

    private ExecuteState getExecuteState() {
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

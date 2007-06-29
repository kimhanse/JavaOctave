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

    private static final String[] CMD_ARRAY =
            { "octave", "--no-history", "--no-init-file", "--no-line-editing", "--no-site-file", "--silent" };

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
     *            This writer will capture all that is written to the octave process via stdin, if null the data will
     *            not be captured.
     * @param stdoutLog
     *            This writer will capture all that is written from the octave process on stdout, if null the data will
     *            not be captured.
     * @param stderrLog
     *            This writer will capture all that is written from the octave process on stderr, if null the data will
     *            not be captured.
     * @param octaveProgram
     *            This is the path to the octave program, if it is null the program 'octave' will be assumed to be in
     *            the PATH.
     * @param environment
     *            The environment for the octave process, if null the process will inherit the environment for the
     *            virtual mashine.
     * @param workingDir
     *            This will be the working dir for the octave process, if null the process will inherit the working dir
     *            of the current process.
     * @throws OctaveException
     */
    public Octave(Writer stdinLog, Writer stdoutLog, Writer stderrLog, File octaveProgram, String[] environment,
            File workingDir) throws OctaveException {
        final String[] cmdArray;
        if (octaveProgram == null) {
            cmdArray = CMD_ARRAY;
        } else {
            cmdArray = CMD_ARRAY.clone();
            cmdArray[0] = octaveProgram.getPath();
        }
        try {
            process = Runtime.getRuntime().exec(cmdArray, environment, workingDir);
        } catch (IOException e) {
            throw new OctaveException(e);
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
            processWriter =
                    new TeeWriter(new NoCloseWriter(stdinLog), new OutputStreamWriter(process.getOutputStream()));
        }
        // Setup octave process
        // readSetup();
    }

    @SuppressWarnings("unused")
    private void readSetup() throws OctaveException {
        try {
            final InputStreamReader setup = new InputStreamReader(getClass().getResourceAsStream("setup.m"));
            final char[] buffer = new char[4096];
            int len;
            while ((len = setup.read(buffer)) != -1) {
                processWriter.write(buffer, 0, len);
            }
            setup.close();
        } catch (IOException e) {
            throw new OctaveException(e);
        }
    }

    /**
     * Will start the octave process in a standard environment.
     * 
     * @param stdinLog
     *            This writer will capture all that is written to the octave process via stdin, if null the data will
     *            not be captured.
     * @param stdoutLog
     *            This writer will capture all that is written from the octave process on stdout, if null the data will
     *            not be captured.
     * @param stderrLog
     *            This writer will capture all that is written from the octave process on stderr, if null the data will
     *            not be captured.
     * @throws OctaveException
     */
    public Octave(Writer stdinLog, Writer stdoutLog, Writer stderrLog) throws OctaveException {
        this(stdinLog, stdoutLog, stderrLog, null, null, null);
    }

    /**
     * Will start the octave process with its output connected to System.out and System.err.
     * 
     * @throws OctaveException
     */
    public Octave() throws OctaveException {
        this(null, new OutputStreamWriter(System.out), new OutputStreamWriter(System.err));
    }

    private Random random = new Random();

    private String generateSpacer() {
        return "-=+X+=- Octave.java spacer -=+X+=- " + random.nextLong() + " -=+X+=-";
    }

    private boolean isSpacer(String string) {
        return string.matches("-=\\+X\\+=- Octave\\.java spacer -=\\+X\\+=- .* -=\\+X\\+=-");
    }

    /**
     * @param inputReader
     * @return Returns a Reader that will return the result from the statements that octave gets from the inputReader
     * @throws OctaveException
     */
    public Reader executeReader(Reader inputReader) throws OctaveException {
        assert check();
        String spacer = generateSpacer();
        assert isSpacer(spacer);
        OctaveInputThread octaveInputThread = new OctaveInputThread(inputReader, processWriter, spacer, this);
        OctaveExecuteReader outputReader = new OctaveExecuteReader(processReader, spacer, octaveInputThread, this);
        setExecuteState(ExecuteState.BOTH_RUNNING);
        octaveInputThread.start();
        return outputReader;
    }

    /**
     * @param inputReader
     * @param echo
     * @throws OctaveException
     */
    public void execute(Reader inputReader, boolean echo) throws OctaveException {
        assert check();
        Reader resultReader = executeReader(inputReader);
        try {
            char[] cbuf = new char[BUFFERSIZE];
            while (true) {
                int len = resultReader.read(cbuf);
                if (len == -1)
                    break;
                if (echo) {
                    stdoutLog.write(cbuf, 0, len);
                    stdoutLog.flush();
                }
            }
            resultReader.close();
        } catch (IOException e) {
            OctaveException octaveException = new OctaveException(e);
            if (getExecuteState() == ExecuteState.DESTROYED) {
                octaveException.setDestroyed(true);
            }
            throw octaveException;
        }
        assert check();
    }

    /**
     * @param reader
     * @throws OctaveException
     */
    public void execute(Reader reader) throws OctaveException {
        execute(reader, true);
    }

    /**
     * @param cmd
     * @param echo
     * @throws OctaveException
     */
    public void execute(String cmd, boolean echo) throws OctaveException {
        execute(new StringReader(cmd), echo);
    }

    /**
     * @param cmd
     * @throws OctaveException
     */
    public void execute(String cmd) throws OctaveException {
        execute(cmd, true);
    }

    /**
     * Convenience overload
     * 
     * @param name
     * @param value
     * @throws OctaveException
     */
    public void set(String name, OctaveType value) throws OctaveException {
        set(Collections.singletonMap(name, value));
    }

    /**
     * @param values
     * @throws OctaveException
     */
    public void set(Map<String, OctaveType> values) throws OctaveException {
        assert check();
        try {
            Reader resultReader = executeReader(OctaveType.octaveReader(values));
            char[] cbuf = new char[BUFFERSIZE];
            int len = resultReader.read(cbuf);
            if (len != -1) {
                String buffer = new String(cbuf, 0, len);
                throw new OctaveException("Unexpected output when setting variable in octave: " + buffer);
            }
            resultReader.close();
        } catch (IOException e) {
            OctaveException octaveException = new OctaveException(e);
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
     * @throws OctaveException
     */
    public BufferedReader get(String name) throws OctaveException {
        assert check();
        BufferedReader resultReader = new BufferedReader(executeReader(new StringReader("save -text - " + name)));
        try {
            String line = processReader.readLine();
            if (line == null || !line.startsWith("# Created by Octave 2.9"))
                throw new OctaveException("Unsupported version of octave " + line);
            line = processReader.readLine();
            String token = "# name: ";
            if (!line.startsWith(token)) {
                if (isSpacer(line)) {
                    throw new OctaveException("no such variable '" + name + "'");
                } else {
                    throw new OctaveException("Expected <" + token + ">, but got <" + line + ">");
                }
            }
            String readname = line.substring(token.length());
            if (!name.equals(readname)) {
                throw new OctaveException("Expected variable named \"" + name + "\" but got one named \"" + readname
                        + "\"");
            }
        } catch (IOException e) {
            OctaveException octaveException = new OctaveException(e);
            if (getExecuteState() == ExecuteState.DESTROYED) {
                octaveException.setDestroyed(true);
            }
            throw octaveException;
        }
        return resultReader;
    }

    /**
     * @throws OctaveException
     */
    public void close() throws OctaveException {
        assert check();
        setExecuteState(ExecuteState.CLOSING);
        try {
            processWriter.write("exit\n");
            processWriter.close();
            String read = processReader.readLine();
            if (read != null) {
                throw new OctaveException("Expected reader to be closed: " + read);
            }
            processReader.close();
        } catch (IOException e) {
            OctaveException octaveException = new OctaveException("reader error", e);
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
     *         was an error OctaveException would be thrown.
     * @throws OctaveException
     *             when the executeState is illegal
     */
    public boolean check() throws OctaveException {
        ExecuteState executeState2 = getExecuteState();
        if (executeState2 != ExecuteState.NONE) {
            OctaveException octaveException = new OctaveException("Failed check(), executeState=" + executeState2);
            if (executeState2 == ExecuteState.DESTROYED) {
                octaveException.setDestroyed(true);
            }
            throw octaveException;
        }
        return true;
    }

    /**
     * @throws OctaveException
     */
    public void destroy() throws OctaveException {
        setExecuteState(ExecuteState.DESTROYED);
        process.destroy();
        try {
            processWriter.close();
        } catch (IOException e) {
            throw new OctaveException(e);
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

    synchronized void setExecuteState(ExecuteState executeState) throws OctaveException {
        // Throw exception with isDestroyed if state changes from DESTROYED
        if (this.executeState == ExecuteState.DESTROYED) {
            OctaveException octaveException =
                    new OctaveException("setExecuteState Error: " + this.executeState + " -> " + executeState);
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
            throw new OctaveException("setExecuteState Error: " + this.executeState + " -> " + executeState);
        }
        log.debug("State changed from " + this.executeState + " to " + executeState);
        this.executeState = executeState;
    }

}

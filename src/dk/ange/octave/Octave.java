package dk.ange.octave;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Random;

import dk.ange.octave.type.OctaveType;
import dk.ange.util.Pipe;
import dk.ange.util.TeeWriter;

public class Octave {

    private static final String[] cmdarray = { "octave", "--no-history",
            "--no-init-file", "--no-line-editing", "--no-site-file", "--silent" };

    private static final int BUFFERSIZE = 1024;

    private Process process;

    private PrintWriter writer;

    private BufferedReader reader;

    private Writer stdout;

    enum ExecuteState {
        NONE, BOTH_RUNNING, WRITER_OK, CLOSING, CLOSED
    }

    private ExecuteState executeState = ExecuteState.NONE;

    public Octave(Writer stdin, Writer stdout, Writer stderr, File dir)
            throws OctaveException {
        this.stdout = stdout;
        try {
            process = Runtime.getRuntime().exec(cmdarray, null, dir);
        } catch (IOException e) {
            throw new OctaveException(e);
        }
        if (stdin == null) {
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    process.getOutputStream())), true);
        } else {
            writer = new PrintWriter(new BufferedWriter(new TeeWriter(stdin,
                    new OutputStreamWriter(process.getOutputStream()))), true);
        }
        reader = new BufferedReader(new InputStreamReader(process
                .getInputStream()));
        new Pipe(new BufferedReader(new InputStreamReader(process
                .getErrorStream())), stderr).start();
    }

    public Octave(Writer stdin, Writer stdout, Writer stderr)
            throws OctaveException {
        this(stdin, stdout, stderr, null);
    }

    public Octave(Writer stdout, Writer stderr) throws OctaveException {
        this(null, stdout, stderr);
    }

    public Octave() throws OctaveException {
        this(null, new OutputStreamWriter(System.out), new OutputStreamWriter(
                System.err));
    }

    private Random random = new Random();

    private String generateSpacer() {
        return "-=+X+=- Octave.java spacer -=+X+=- " + random.nextLong()
                + " -=+X+=-";
    }

    public Reader execute(Reader inputReader) throws OctaveException {
        String spacer = generateSpacer();
        OctaveInputThread octaveInputThread = new OctaveInputThread(
                inputReader, writer, spacer, this);
        OctaveExecuteReader outputReader = new OctaveExecuteReader(reader,
                spacer, octaveInputThread, this);
        setExecuteState(ExecuteState.BOTH_RUNNING);
        octaveInputThread.start();
        return outputReader;
    }

    public void execute(Reader inputReader, boolean echo)
            throws OctaveException {
        Reader resultReader = execute(inputReader);
        try {
            char[] cbuf = new char[BUFFERSIZE];
            while (true) {
                int len = resultReader.read(cbuf);
                if (len == -1)
                    break;
                if (echo) {
                    stdout.write(cbuf, 0, len);
                    stdout.flush();
                }
            }
            resultReader.close();
        } catch (IOException e1) {
            throw new OctaveException(e1);
        }
    }

    public void execute(String cmd, boolean echo) throws OctaveException {
        execute(new StringReader(cmd), echo);
    }

    public void execute(String cmd) throws OctaveException {
        execute(cmd, true);
    }

    public void set(String name, OctaveType value) throws OctaveException {
        Reader resultReader = execute(value.octaveReader(name));
        try {
            char[] cbuf = new char[BUFFERSIZE];
            int len = resultReader.read(cbuf);
            if (len != -1) {
                String buffer = new String(cbuf, 0, len);
                throw new OctaveException(
                        "Unexpected output when setting variable in octave: "
                                + buffer);
            }
            resultReader.close();
        } catch (IOException e1) {
            throw new OctaveException(e1);
        }
    }

    public BufferedReader get(String name) throws OctaveException {
        BufferedReader resultReader = new BufferedReader(
                execute(new StringReader("save -text - " + name)));
        try {
            String line = reader.readLine();
            if (line == null || !line.startsWith("# Created by Octave 2.9"))
                throw new OctaveException("huh? " + line);
            line = reader.readLine();
            if (line == null || !line.equals("# name: " + name))
                throw new OctaveException("huh? " + line);
        } catch (IOException e) {
            throw new OctaveException(e);
        }
        return resultReader;
    }

    public void close() throws OctaveException {
        setExecuteState(ExecuteState.CLOSING);
        writer.write("exit\n");
        writer.close();
        try {
            String read = reader.readLine();
            if (read != null) {
                throw new OctaveException("Expected reader to be closed: "
                        + read);
            }
            reader.close();
        } catch (IOException e) {
            throw new OctaveException("reader error", e);
        }
        setExecuteState(ExecuteState.CLOSED);
        try {
            stdout.close();
        } catch (IOException e) {
            throw new OctaveException("stdout error", e);
        }
    }

    public void destroy() {
        process.destroy();
    }

    synchronized void setExecuteState(ExecuteState executeState)
            throws OctaveException {
        // Accepted transitions:
        // - NONE -> BOTH_RUNNING
        // - BOTH_RUNNING -> WRITER_OK
        // - WRITER_OK -> NONE
        // - NONE -> CLOSING
        // - CLOSING -> CLOSED
        if (!(this.executeState == ExecuteState.NONE
                && executeState == ExecuteState.BOTH_RUNNING
                || this.executeState == ExecuteState.BOTH_RUNNING
                && executeState == ExecuteState.WRITER_OK
                || this.executeState == ExecuteState.WRITER_OK
                && executeState == ExecuteState.NONE
                || this.executeState == ExecuteState.NONE
                && executeState == ExecuteState.CLOSING || this.executeState == ExecuteState.CLOSING
                && executeState == ExecuteState.CLOSED)) {
            throw new OctaveException("setExecuteState Error: "
                    + this.executeState + " -> " + executeState);
        }
        this.executeState = executeState;
    }

}

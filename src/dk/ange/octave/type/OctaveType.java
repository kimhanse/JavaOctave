package dk.ange.octave.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import dk.ange.octave.OctaveException;

/**
 * @author Kim Hansen
 * 
 * Common interface for the octave types.
 */
public abstract class OctaveType {

    /**
     * @param values
     * @return Returns a Reader from which the octave input version of values can be read.
     * @throws OctaveException
     */
    static public Reader octaveReader(Map<String, OctaveType> values) throws OctaveException {
        PipedReader pipedReader = new PipedReader();
        PipedWriter pipedWriter = new PipedWriter();
        try {
            pipedWriter.connect(pipedReader);
        } catch (IOException e) {
            throw new OctaveException(e);
        }
        ToOctaveMultiWriter toOctaveWriter = new ToOctaveMultiWriter(values, pipedWriter);
        toOctaveWriter.start();
        return pipedReader;
    }

    private static class ToOctaveMultiWriter extends Thread {

        final Map<String, OctaveType> octaveTypes;

        final PipedWriter pipedWriter;

        /**
         * @param octaveTypes
         * @param pipedWriter
         */
        public ToOctaveMultiWriter(Map<String, OctaveType> octaveTypes, PipedWriter pipedWriter) {
            this.octaveTypes = octaveTypes;
            this.pipedWriter = pipedWriter;
        }

        @Override
        public void run() {
            try {
                // Enter octave in "read data from input mode"
                pipedWriter.write("load(\"-text\", \"-\")\n");
                // Push the data into octave
                for (Map.Entry<String, OctaveType> entry : octaveTypes.entrySet()) {
                    entry.getValue().save(entry.getKey(), pipedWriter);
                }
                // Exit octave from read data mode
                pipedWriter.write("# name: \n");
                pipedWriter.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    @Override
    public String toString() {
        try {
            return toText("ans");
        } catch (OctaveException e) {
            e.printStackTrace();
            return "[invalid octavetype: " + e.getMessage() + "]";
        }
    }

    /**
     * @param name
     * @return Text to feed to 'load -text -' to define the variable
     * @throws OctaveException
     */
    public String toText(String name) throws OctaveException {
        StringWriter writer = new StringWriter();
        try {
            save(name, writer);
        } catch (IOException e) {
            throw new OctaveException(e);
        }
        return writer.getBuffer().toString();

    }

    /**
     * Utility function.
     * 
     * @param reader
     * @return next line from reader
     * @throws OctaveException
     */
    static String readerReadLine(BufferedReader reader) throws OctaveException {
        try {
            String line = reader.readLine();
            if (line == null)
                throw new OctaveException("Pipe to octave-process broken");
            return line;
        } catch (IOException e) {
            throw new OctaveException(e);
        }
    }

    /**
     * Utility function
     * 
     * @param reader
     * @return next line from reader without modifying the "file pointer", meaning that the next call to readLine or
     *         peekLine will return the same line.
     * @throws OctaveException
     *             Note: The line to be read must be less than 1000 characters.
     */
    static String readerPeekLine(BufferedReader reader) throws OctaveException {
        try {
            reader.mark(1000);
            String line = reader.readLine();
            reader.reset();
            if (line == null) {
                throw new OctaveException("Pipe to octave-process broken");
            }
            return line;
        } catch (IOException e) {
            throw new OctaveException(e);
        }
    }

    /**
     * This is almost the same as Double.parseDouble(), but it handles a few more versions of infinity
     * 
     * @param string
     * @return The parsed Double
     */
    protected double parseDouble(String string) {
        if ("Inf".equals(string)) {
            return Double.POSITIVE_INFINITY;
        }
        if ("-Inf".equals(string)) {
            return Double.NEGATIVE_INFINITY;
        }
        return Double.parseDouble(string);
    }

    /**
     * @param reader
     * @return octavetype read from reader
     * @throws OctaveException
     *             if read failed.
     */
    static public OctaveType readOctaveType(BufferedReader reader) throws OctaveException {
        return readOctaveType(reader, true);
    }

    /**
     * @param reader
     * @param close
     *            whether to close the stream. Really should be true by default, but Java....
     * @return octavetype read from reader
     * @throws OctaveException
     *             if read failed.
     */
    static public OctaveType readOctaveType(BufferedReader reader, boolean close) throws OctaveException {
        String line = readerPeekLine(reader);
        final String TYPE = "# type: ";
        if (!line.startsWith(TYPE)) {
            throw new OctaveException("Expected <" + TYPE + "> got <" + line + ">");
        }
        String type = line.substring(TYPE.length());
        final OctaveType rv;
        if ("struct".equals(type)) {
            rv = new OctaveStruct(reader, close);
        } else if ("matrix".equals(type)) {
            rv = new OctaveMatrix(reader, close);
        } else if ("scalar".equals(type)) {
            rv = new OctaveScalar(reader, close);
        } else if ("string".equals(type)) {
            rv = new OctaveString(reader, close);
        } else if ("cell".equals(type)) {
            rv = new OctaveCell(reader, close);
        } else {
            rv = null;
        }
        return rv;
    }

    /**
     * Dumps this value to writer in format suitable for reading by Octave with load("-text", ...)
     * 
     * @param name
     * 
     * @param writer
     * @throws IOException
     */
    abstract public void save(String name, Writer writer) throws IOException;

    /**
     * @param type
     * @return a (shallow) copy of type, or null or type is null
     */
    public static OctaveType copy(OctaveType type) {
        return (type != null) ? type.makecopy() : null;
    }

    /**
     * @return a copy of this.
     */
    public abstract OctaveType makecopy();

}

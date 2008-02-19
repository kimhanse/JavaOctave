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
package dk.ange.octave.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import dk.ange.octave.exception.OctaveException;
import dk.ange.octave.exception.OctaveIOException;
import dk.ange.octave.exception.OctaveParseException;

/**
 * @author Kim Hansen
 * 
 * Common interface for the octave types.
 */
public abstract class OctaveType implements Serializable {

    /**
     * @param values
     * @return Returns a Reader from which the octave input version of values can be read.
     * @throws OctaveException
     */
    static public Reader octaveReader(final Map<String, OctaveType> values) throws OctaveException {
        final PipedReader pipedReader = new PipedReader();
        final PipedWriter pipedWriter = new PipedWriter();
        try {
            pipedWriter.connect(pipedReader);
        } catch (final IOException e) {
            throw new OctaveIOException(e);
        }
        final ToOctaveMultiWriter toOctaveWriter = new ToOctaveMultiWriter(values, pipedWriter);
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
        public ToOctaveMultiWriter(final Map<String, OctaveType> octaveTypes, final PipedWriter pipedWriter) {
            this.octaveTypes = octaveTypes;
            this.pipedWriter = pipedWriter;
        }

        @Override
        public void run() {
            try {
                // Enter octave in "read data from input mode"
                pipedWriter.write("load(\"-text\", \"-\")\n");
                // Push the data into octave
                for (final Map.Entry<String, OctaveType> entry : octaveTypes.entrySet()) {
                    entry.getValue().save(entry.getKey(), pipedWriter);
                }
                // Exit octave from read data mode
                pipedWriter.write("# name: \n");
                pipedWriter.close();
            } catch (final IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    @Override
    public String toString() {
        try {
            return toText("ans");
        } catch (final OctaveException e) {
            e.printStackTrace();
            return "[invalid octavetype: " + e.getMessage() + "]";
        }
    }

    /**
     * @param name
     * @return Text to feed to 'load -text -' to define the variable
     * @throws OctaveException
     */
    public String toText(final String name) throws OctaveException {
        final StringWriter writer = new StringWriter();
        try {
            save(name, writer);
        } catch (final IOException e) {
            throw new OctaveIOException(e);
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
    static String readerReadLine(final BufferedReader reader) throws OctaveException {
        try {
            final String line = reader.readLine();
            if (line == null) {
                throw new OctaveIOException("Pipe to octave-process broken");
            }
            return line;
        } catch (final IOException e) {
            throw new OctaveIOException(e);
        }
    }

    /**
     * Utility function
     * 
     * @param reader
     * @return next line from reader without modifying the "file pointer", meaning that the next call to readLine or
     *         peekLine will return the same line.
     * @throws OctaveException
     *                 Note: The line to be read must be less than 1000 characters.
     */
    static String readerPeekLine(final BufferedReader reader) throws OctaveException {
        try {
            reader.mark(1000);
            final String line = reader.readLine();
            reader.reset();
            if (line == null) {
                throw new OctaveIOException("Pipe to octave-process broken");
            }
            return line;
        } catch (final IOException e) {
            throw new OctaveIOException(e);
        }
    }

    /**
     * This is almost the same as Double.parseDouble(), but it handles a few more versions of infinity
     * 
     * @param string
     * @return The parsed Double
     */
    protected double parseDouble(final String string) {
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
     *                 if read failed.
     */
    static public OctaveType readOctaveType(final BufferedReader reader) throws OctaveException {
        return readOctaveType(reader, true);
    }

    /**
     * @param reader
     * @param close
     *                whether to close the stream. Really should be true by default
     * @return octavetype read from reader
     * @throws OctaveException
     *                 if read failed.
     */
    static public OctaveType readOctaveType(final BufferedReader reader, final boolean close) throws OctaveException {
        final String line = readerPeekLine(reader);
        final String TYPE = "# type: ";
        if (!line.startsWith(TYPE)) {
            throw new OctaveParseException("Expected <" + TYPE + "> got <" + line + ">");
        }
        final String type = line.substring(TYPE.length());
        final OctaveType rv;
        if ("struct".equals(type)) {
            rv = new OctaveStruct(reader, close);
        } else if ("matrix".equals(type)) {
            rv = new OctaveNdMatrix(reader, close);
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
    public static OctaveType copy(final OctaveType type) {
        return (type != null) ? type.makecopy() : null;
    }

    /**
     * @return a copy of this.
     */
    public abstract OctaveType makecopy();

}

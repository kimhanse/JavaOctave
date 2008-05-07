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
package dk.ange.octave.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import dk.ange.octave.exception.OctaveClassCastException;
import dk.ange.octave.exception.OctaveIOException;
import dk.ange.octave.exception.OctaveParseException;
import dk.ange.octave.exec.OctaveExec;
import dk.ange.octave.exec.ReaderWriteFunctor;
import dk.ange.octave.exec.WriteFunctor;
import dk.ange.octave.io.spi.OctaveDataReader;
import dk.ange.octave.io.spi.OctaveDataWriter;
import dk.ange.octave.type.OctaveType;

/**
 * The object controlling IO of Octave data
 */
public final class OctaveIO {

    private final OctaveExec octaveExec;

    /**
     * @param octaveExec
     */
    public OctaveIO(final OctaveExec octaveExec) {
        this.octaveExec = octaveExec;
    }

    /**
     * @param values
     */
    public void set(final Map<String, OctaveType> values) {
        final StringWriter outputWriter = new StringWriter();
        octaveExec.execute(new DataWriteFunctor(values), outputWriter);
        final String output = outputWriter.toString();
        if (output.length() != 0) {
            throw new IllegalStateException("Unexpected output, " + output);
        }
    }

    /**
     * @param <T>
     *                Type of return value
     * @param name
     * @return Returns the value of the variable from octave
     * @throws OctaveClassCastException
     *                 if the value can not be cast to T
     */
    @SuppressWarnings("unchecked")
    public <T extends OctaveType> T get(final String name) {
        final WriteFunctor writeFunctor = new ReaderWriteFunctor(new StringReader("save -text - " + name));
        final DataReadFunctor readFunctor = new DataReadFunctor(name);
        octaveExec.eval(writeFunctor, readFunctor);
        final OctaveType ot = readFunctor.getData();
        final T t;
        try {
            // This is the "unchecked" cast
            t = (T) ot;
        } catch (final ClassCastException e) {
            throw new OctaveClassCastException(e);
        }
        return t;
    }

    /**
     * Utility function.
     * 
     * @param reader
     * @return next line from reader
     */
    public static String readerReadLine(final BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (final IOException e) {
            throw new OctaveIOException(e);
        }
    }

    /**
     * @param reader
     * @return octavetype read from reader
     */
    public static OctaveType read(final BufferedReader reader) {
        final String line = OctaveIO.readerReadLine(reader);
        final String TYPE = "# type: ";
        if (!line.startsWith(TYPE)) {
            throw new OctaveParseException("Expected <" + TYPE + "> got <" + line + ">");
        }
        final String type = line.substring(TYPE.length());
        final OctaveDataReader dataReader = OctaveDataReader.getOctaveDataReader(type);
        if (dataReader == null) {
            throw new OctaveParseException("Unknown octave type, type='" + type + "'");
        }
        return dataReader.read(reader);
    }

    /**
     * @param writer
     * @param octaveType
     * @throws IOException
     */
    public static void write(final Writer writer, final OctaveType octaveType) throws IOException {
        final OctaveDataWriter dataWriter = OctaveDataWriter.getOctaveDataWriter(octaveType.getClass());
        if (dataWriter == null) {
            throw new OctaveParseException("Unknown type, " + octaveType.getClass());
        }
        dataWriter.write(writer, octaveType);
    }

    /**
     * @param writer
     * @param name
     * @param octaveType
     * @throws IOException
     */
    public static void write(final Writer writer, final String name, final OctaveType octaveType) throws IOException {
        writer.write("# name: " + name + "\n");
        write(writer, octaveType);
    }

    /**
     * @param octaveType
     * @param name
     * @return The result from saving the value octaveType in octave -text format
     */
    public static String toText(final OctaveType octaveType, final String name) {
        try {
            final Writer writer = new java.io.StringWriter();
            write(writer, name, octaveType);
            return writer.toString();
        } catch (final IOException e) {
            throw new OctaveIOException(e);
        }
    }

    /**
     * @param octaveType
     * @return toText(octaveType, "ans")
     */
    public static String toText(final OctaveType octaveType) {
        return toText(octaveType, "ans");
    }

}

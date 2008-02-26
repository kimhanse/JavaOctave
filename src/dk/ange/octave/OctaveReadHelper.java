/*
 * Copyright 2008 Ange Optimization ApS
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

import java.io.BufferedReader;
import java.io.IOException;

import dk.ange.octave.exception.OctaveException;
import dk.ange.octave.exception.OctaveIOException;
import dk.ange.octave.exception.OctaveParseException;
import dk.ange.octave.type.OctaveCell;
import dk.ange.octave.type.OctaveNdMatrix;
import dk.ange.octave.type.OctaveScalar;
import dk.ange.octave.type.OctaveString;
import dk.ange.octave.type.OctaveStruct;
import dk.ange.octave.type.OctaveType;

/**
 * Utility class for static functions related to reading octave values
 */
public final class OctaveReadHelper {

    private OctaveReadHelper() {
        throw new UnsupportedOperationException("Do not instantiate");
    }

    /**
     * @param reader
     * @param close
     *                whether to close the stream. Really should be true by default
     * @return octavetype read from reader
     */
    public static OctaveType readOctaveType(final BufferedReader reader, final boolean close) {
        final String line = OctaveReadHelper.readerPeekLine(reader);
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
     * Calls readOctaveType(reader, true)
     * 
     * @param reader
     * @return octavetype read from reader
     */
    public static OctaveType readOctaveType(final BufferedReader reader) {
        return readOctaveType(reader, true);
    }

    /**
     * This is almost the same as Double.parseDouble(), but it handles a few more versions of infinity
     * 
     * @param string
     * @return The parsed Double
     */
    public static final double parseDouble(final String string) {
        if ("Inf".equals(string)) {
            return Double.POSITIVE_INFINITY;
        }
        if ("-Inf".equals(string)) {
            return Double.NEGATIVE_INFINITY;
        }
        return Double.parseDouble(string);
    }

    /**
     * Utility function.
     * 
     * @param reader
     * @return next line from reader
     * @throws OctaveException
     */
    public static String readerReadLine(final BufferedReader reader) {
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
    public static String readerPeekLine(final BufferedReader reader) {
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

}

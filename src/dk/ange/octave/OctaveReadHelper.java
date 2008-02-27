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

/**
 * Utility class for static functions related to reading octave values
 */
public final class OctaveReadHelper {

    private OctaveReadHelper() {
        throw new UnsupportedOperationException("Do not instantiate");
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

}

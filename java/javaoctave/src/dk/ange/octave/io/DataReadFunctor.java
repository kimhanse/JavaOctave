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
package dk.ange.octave.io;

import static dk.ange.octave.io.OctaveIO.readerReadLine;

import java.io.BufferedReader;
import java.io.Reader;

import dk.ange.octave.exception.OctaveParseException;
import dk.ange.octave.exec.ReadFunctor;
import dk.ange.octave.type.OctaveType;

/**
 * Functor that reads a single variable
 */
final class DataReadFunctor implements ReadFunctor {

    private final String name;

    private OctaveType data;

    /**
     * @param name
     */
    public DataReadFunctor(final String name) {
        this.name = name;
    }

    public void doReads(final Reader reader) {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        String line = readerReadLine(bufferedReader);
        if (line == null || !line.startsWith("# Created by Octave")) {
            throw new OctaveParseException("Not created by Octave?: '" + line + "'");
        }
        line = readerReadLine(bufferedReader);
        if (line == null) {
            // try {
            // bufferedReader.close(); // Should .close() be done by caller?
            // } catch (final IOException e) {
            // throw new OctaveIOException(e);
            // }
            throw new OctaveParseException("no such variable '" + name + "'");
        }
        final String token = "# name: ";
        if (!line.startsWith(token)) {
            throw new OctaveParseException("Expected <" + token + ">, but got <" + line + ">");
        }
        final String readname = line.substring(token.length());
        if (!name.equals(readname)) {
            throw new OctaveParseException("Expected variable named \"" + name + "\" but got one named \"" + readname
                    + "\"");
        }
        data = OctaveIO.read(bufferedReader);
    }

    /**
     * @return the data
     */
    public OctaveType getData() {
        return data;
    }

}

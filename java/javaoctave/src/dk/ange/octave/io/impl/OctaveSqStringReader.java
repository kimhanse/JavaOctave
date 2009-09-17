/*
 * Copyright 2008, 2009 Ange Optimization ApS
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
package dk.ange.octave.io.impl;

import java.io.BufferedReader;

import dk.ange.octave.exception.OctaveParseException;
import dk.ange.octave.io.OctaveIO;
import dk.ange.octave.io.spi.OctaveDataReader;
import dk.ange.octave.type.OctaveString;

/**
 * The reader of sq_string
 */
public final class OctaveSqStringReader extends OctaveDataReader {

    @Override
    public String octaveType() {
        return "sq_string";
    }

    @Override
    public OctaveString read(final BufferedReader reader) {
        final String elements = OctaveIO.readerReadLine(reader);
        if (!elements.equals("# elements: 1")) {
            throw new OctaveParseException("Only implementet for single-line strings '" + elements + "'");
        }
        final String length = OctaveIO.readerReadLine(reader);
        if (!length.startsWith("# length: ")) {
            throw new OctaveParseException("Parse error in String, line='" + length + "'");
        }
        final String string = OctaveIO.readerReadLine(reader);
        if (string.contains("\\")) {
            throw new OctaveParseException("Handling of escape char (\\) not done, line='" + string + "'");
        }
        return new OctaveString(string);
    }

}

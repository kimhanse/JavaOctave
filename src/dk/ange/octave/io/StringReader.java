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

import java.io.BufferedReader;

import dk.ange.octave.OctaveReadHelper;
import dk.ange.octave.exception.OctaveParseException;
import dk.ange.octave.type.OctaveString;
import dk.ange.octave.type.OctaveType;

/**
 * The reader of string
 */
public final class StringReader implements OctaveDataReader {

    public String octaveType() {
        return "string";
    }

    public OctaveType read(final BufferedReader reader) {
        String line;
        line = OctaveReadHelper.readerReadLine(reader);
        if (!line.equals("# type: string")) {
            throw new OctaveParseException("Wrong type of variable");
        }
        line = OctaveReadHelper.readerReadLine(reader);
        if (!line.equals("# elements: 1")) {
            throw new OctaveParseException("Only implementet for single-line strings '" + line + "'");
        }
        line = OctaveReadHelper.readerReadLine(reader);
        if (!line.startsWith("# length: ")) {
            throw new OctaveParseException("Parse error in String");
        }
        final String string = OctaveReadHelper.readerReadLine(reader);
        return new OctaveString(string);
    }

}

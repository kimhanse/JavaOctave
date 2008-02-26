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
import dk.ange.octave.type.OctaveScalar;
import dk.ange.octave.type.OctaveType;

/**
 * The reader of scalar
 */
public final class ScalarReader implements OctaveDataReader {

    public String octaveType() {
        return "scalar";
    }

    public OctaveType read(final BufferedReader reader) {
        String line;
        line = OctaveReadHelper.readerReadLine(reader);
        final String token = "# type: scalar";
        if (!line.equals(token)) {
            throw new OctaveParseException("Expected <" + token + ">, but got <" + line + ">\n");
        }
        line = OctaveReadHelper.readerReadLine(reader);
        final double value = OctaveReadHelper.parseDouble(line);
        return new OctaveScalar(value);
    }

}

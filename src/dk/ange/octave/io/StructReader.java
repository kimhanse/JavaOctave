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
import java.util.HashMap;
import java.util.Map;

import dk.ange.octave.OctaveReadHelper;
import dk.ange.octave.exception.OctaveParseException;
import dk.ange.octave.type.OctaveCell;
import dk.ange.octave.type.OctaveStruct;
import dk.ange.octave.type.OctaveType;

/**
 * The reader of struct
 */
public final class StructReader implements OctaveDataReader {

    public String octaveType() {
        return "struct";
    }

    public OctaveType read(final BufferedReader reader) {
        String line;
        final String TYPE_STRUCT = "# type: struct";
        final String TYPE_GLOBAL_STRUCT = "# type: global struct";
        line = OctaveReadHelper.readerReadLine(reader);
        if (!line.equals(TYPE_STRUCT) && !line.equals(TYPE_GLOBAL_STRUCT)) {
            throw new OctaveParseException("Variable was not a struct or global struct, " + line);
        }
        // # length: 4
        line = OctaveReadHelper.readerReadLine(reader);

        final String LENGTH = "# length: ";
        if (!line.startsWith(LENGTH)) {
            throw new OctaveParseException("Expected <" + LENGTH + "> got <" + line + ">");
        }
        final int length = Integer.valueOf(line.substring(LENGTH.length())); // only used during conversion
        final Map<String, OctaveType> data = new HashMap<String, OctaveType>();
        for (int i = 0; i < length; i++) {
            // # name: elemmatrix
            final String NAME = "# name: ";
            line = OctaveReadHelper.readerReadLine(reader);
            if (!line.startsWith(NAME)) {
                throw new OctaveParseException("Expected <" + NAME + "> got <" + line + ">");
            }
            final String subname = line.substring(NAME.length());
            final OctaveCell cell = new OctaveCell(reader, false);
            // If the cell is a 1x1, move up the value
            if (cell.getRowDimension() == 1 && cell.getColumnDimension() == 1) {
                final OctaveType value = cell.get(1, 1);
                data.put(subname, value);
            } else {
                data.put(subname, cell);
            }
        }
        return new OctaveStruct(data);
    }

}

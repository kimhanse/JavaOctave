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
package dk.ange.octave.io.impl;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import dk.ange.octave.OctaveIO;
import dk.ange.octave.exception.OctaveParseException;
import dk.ange.octave.io.OctaveDataReader;
import dk.ange.octave.type.OctaveCell;
import dk.ange.octave.type.OctaveStruct;
import dk.ange.octave.type.OctaveType;

/**
 * The reader of struct
 */
public final class StructReader implements OctaveDataReader {

    private final static CellReader cellReader = new CellReader();

    public String octaveType() {
        return "struct";
    }

    public OctaveStruct read(final BufferedReader reader) {
        String line;

        // # length: 4
        line = OctaveIO.readerReadLine(reader);
        final String LENGTH = "# length: ";
        if (!line.startsWith(LENGTH)) {
            throw new OctaveParseException("Expected <" + LENGTH + "> got <" + line + ">");
        }
        final int length = Integer.valueOf(line.substring(LENGTH.length())); // only used during conversion

        final Map<String, OctaveType> data = new HashMap<String, OctaveType>();

        for (int i = 0; i < length; i++) {
            // # name: elemmatrix
            final String NAME = "# name: ";
            line = OctaveIO.readerReadLine(reader);
            if (!line.startsWith(NAME)) {
                throw new OctaveParseException("Expected <" + NAME + "> got <" + line + ">");
            }
            final String subname = line.substring(NAME.length());

            final String CELL = "# type: cell";
            line = OctaveIO.readerReadLine(reader);
            if (!line.equals(CELL)) {
                throw new OctaveParseException("Expected <" + CELL + "> got <" + line + ">");
            }

            final OctaveCell cell = cellReader.read(reader);
            // If the cell is a 1x1, move up the value
            if (cell.getRowDimension() == 1 && cell.getColumnDimension() == 1) {
                final OctaveType value = cell.get(1, 1);
                data.put(subname, value);
            } else {
                // XXX do we support this ?
                data.put(subname, cell);
            }
        }

        return new OctaveStruct(data);
    }

}

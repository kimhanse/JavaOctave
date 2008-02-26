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
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import dk.ange.octave.OctaveReadHelper;
import dk.ange.octave.exception.OctaveIOException;
import dk.ange.octave.exception.OctaveParseException;

/**
 * @author Kim Hansen
 * @author Esben Mose Hansen
 */
public class OctaveStruct extends OctaveType {

    private static final long serialVersionUID = 430390185317050230L;

    private Map<String, OctaveType> data = new HashMap<String, OctaveType>();

    /**
     */
    public OctaveStruct() {
        // Stupid warning suppression
    }

    /**
     * @param reader
     */
    public OctaveStruct(final BufferedReader reader) {
        this(reader, true);
    }

    /**
     * @param reader
     * @param close
     *                whether to close the stream. Really should be true by default
     */
    public OctaveStruct(final BufferedReader reader, final boolean close) {
        try {
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
            if (close) {
                reader.close();
            }
        } catch (final IOException e) {
            throw new OctaveIOException(e);
        }
    }

    private OctaveStruct(final Map<String, OctaveType> data) {
        this.data = data;
    }

    /**
     * @param name
     * @param value
     */
    public void set(final String name, final OctaveType value) {
        data.put(name, value);
    }

    @Override
    public void save(final Writer writer) throws IOException {
        writer.write("# type: struct\n# length: " + data.size() + "\n");
        for (final Map.Entry<String, OctaveType> entry : data.entrySet()) {
            final String subname = entry.getKey();
            final OctaveType value = entry.getValue();
            writer.write("# name: " + subname + "\n# type: cell\n# rows: 1\n# columns: 1\n");
            value.save("<cell-element>", writer);
        }

    }

    /**
     * @param key
     * @return (shallow copy of) value for this key, or null if key isn't there.
     */
    public OctaveType get(final String key) {
        return OctaveType.copy(data.get(key));
    }

    @Override
    public OctaveStruct makecopy() {
        return new OctaveStruct(data);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof OctaveStruct) {
            final OctaveStruct struct = (OctaveStruct) obj;
            return data.equals(struct.data);

        }
        return false;
    }

}

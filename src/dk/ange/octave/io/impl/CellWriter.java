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

import java.io.IOException;
import java.io.Writer;

import dk.ange.octave.io.OctaveIO;
import dk.ange.octave.io.spi.OctaveDataWriter;
import dk.ange.octave.type.OctaveCell;
import dk.ange.octave.type.OctaveType;

/**
 * The writer of OctaveCell
 */
public final class CellWriter extends OctaveDataWriter {

    @Override
    public Class<? extends OctaveType> javaType() {
        return OctaveCell.class;
    }

    @Override
    public void write(final Writer writer, final OctaveType octaveType) throws IOException {
        final OctaveCell octaveCell = (OctaveCell) octaveType;
        final int rows = octaveCell.getRowDimension();
        final int columns = octaveCell.getColumnDimension();
        writer.write("# type: cell\n# rows: " + rows + "\n# columns: " + columns + "\n");
        for (int c = 1; c <= columns; ++c) {
            for (int r = 1; r <= rows; ++r) {
                final OctaveType value = octaveCell.get(r, c);
                OctaveIO.write(writer, "<cell-element>", value);
            }
            writer.write("\n");
        }
    }

}

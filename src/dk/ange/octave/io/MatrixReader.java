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
import dk.ange.octave.type.OctaveNdMatrix;
import dk.ange.octave.type.OctaveType;

/**
 * The reader of matrix
 */
public final class MatrixReader implements OctaveDataReader {

    public String octaveType() {
        return "matrix";
    }

    public OctaveType read(final BufferedReader reader) {
        String line;
        // # type: matrix
        line = OctaveReadHelper.readerReadLine(reader);
        if (!line.equals("# type: matrix")) {
            throw new OctaveParseException("Wrong type of variable, " + line);
        }
        // 2d or 2d+?
        line = OctaveReadHelper.readerPeekLine(reader);
        if (line.startsWith("# rows: ")) {
            return read2dmatrix(reader);
        } else if (line.startsWith("# ndims: ")) {
            return readVectorizedMatrix(reader);
        } else {
            throw new OctaveParseException("Expected <# rows: > or <# ndims: >, but got <" + line + ">");
        }
    }

    private OctaveNdMatrix readVectorizedMatrix(final BufferedReader reader) {
        String line;
        final String NDIMS = "# ndims: ";
        line = OctaveReadHelper.readerReadLine(reader);
        if (!line.startsWith(NDIMS)) {
            throw new OctaveParseException("Expected <" + NDIMS + ">, but got <" + line + ">");
        }
        final int ndims = Integer.parseInt(line.substring(NDIMS.length()));
        line = OctaveReadHelper.readerReadLine(reader);
        final String[] split = line.substring(1).split(" ");
        if (split.length != ndims) {
            throw new OctaveParseException("Expected " + ndims + " dimesion, but got " + (split.length)
                    + " (line was <" + line + ">)");
        }
        final int[] size = new int[split.length];
        for (int dim = 0; dim < split.length; dim++) {
            size[dim] = Integer.parseInt(split[dim]);
        }
        final double[] data = new double[product(size)];
        for (int idx = 0; idx < data.length; idx++) {
            line = OctaveReadHelper.readerReadLine(reader);
            data[idx] = OctaveReadHelper.parseDouble(line);
        }
        return new OctaveNdMatrix(data, size);
    }

    private OctaveNdMatrix read2dmatrix(final BufferedReader reader) {
        String line;
        // # rows: 1
        line = OctaveReadHelper.readerReadLine(reader);
        if (!line.startsWith("# rows: ")) {
            throw new OctaveParseException("Expected <# rows: > got <" + line + ">");
        }
        final int rows = Integer.valueOf(line.substring(8));
        // # columns: 3
        line = OctaveReadHelper.readerReadLine(reader);
        if (!line.startsWith("# columns: ")) {
            throw new OctaveParseException("Expected <# columns: > got <" + line + ">");
        }
        final int columns = Integer.valueOf(line.substring(11));
        // 1 2 3
        final int[] size = new int[2];
        size[0] = rows;
        size[1] = columns;
        final double[] data = new double[rows * columns];
        for (int r = 1; r <= rows; ++r) {
            line = OctaveReadHelper.readerReadLine(reader);
            final String[] split = line.split(" ");
            if (split.length != columns + 1) {
                throw new OctaveParseException("Error in matrix-format: '" + line + "'");
            }
            for (int c = 1; c < split.length; c++) {
                data[(r - 1) + (c - 1) * rows] = OctaveReadHelper.parseDouble(split[c]);
            }
        }
        return new OctaveNdMatrix(data, size);
    }

    /**
     * @param ns
     * @return product of rs
     */
    private static int product(final int... ns) {
        int p = 1;
        for (final int n : ns) {
            p *= n;
        }
        return p;
    }

}

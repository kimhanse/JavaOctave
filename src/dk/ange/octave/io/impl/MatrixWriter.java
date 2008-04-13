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

import dk.ange.octave.io.OctaveDataWriter;
import dk.ange.octave.type.OctaveNdMatrix;
import dk.ange.octave.type.OctaveType;

/**
 * The writer of OctaveNdMatrix
 */
public final class MatrixWriter implements OctaveDataWriter {

    public Class<? extends OctaveType> javaType() {
        return OctaveNdMatrix.class;
    }

    public void write(final Writer writer, final OctaveType octaveType) throws IOException {
        final OctaveNdMatrix octaveNdMatrix = (OctaveNdMatrix) octaveType;
        writer.write("# type: matrix\n");
        if (octaveNdMatrix.getSize().length > 2) {
            saveDataVectorized(writer, octaveNdMatrix);
        } else {
            saveData2d(writer, octaveNdMatrix);
        }
    }

    private void saveData2d(final Writer writer, final OctaveNdMatrix octaveNdMatrix) throws IOException {
        final int[] size = octaveNdMatrix.getSize();
        final double[] data = octaveNdMatrix.getData();
        final int nrows = size[0];
        final int ncols = size.length > 1 ? size[1] : 1;
        writer.write("# rows: " + nrows + "\n# columns: " + ncols + "\n");
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                writer.write(" " + data[row + col * nrows]);
            }
            writer.write('\n');
        }
        writer.write("\n");
    }

    private void saveDataVectorized(final Writer writer, final OctaveNdMatrix octaveNdMatrix) throws IOException {
        final int[] size = octaveNdMatrix.getSize();
        final double[] data = octaveNdMatrix.getData();
        writer.write("# ndims: " + size.length + "\n");
        for (final int sdim : size) {
            writer.write(" " + sdim);
        }
        for (final double d : data) {
            writer.write("\n " + d);
        }
        writer.write("\n\n");
    }

}

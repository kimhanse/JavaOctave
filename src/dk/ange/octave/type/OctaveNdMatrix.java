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
import java.util.Arrays;

import dk.ange.octave.OctaveException;

/**
 * @author Kim Hansen
 */
public class OctaveNdMatrix extends OctaveType {

    private static final long serialVersionUID = 4697636627793706177L;

    /**
     * The data, vectorized.
     */
    protected double[] data;

    /**
     * The dimensions, rows x columns x depth x ....
     */
    protected int[] size;

    /**
     * @param reader
     * @throws OctaveException
     */
    public OctaveNdMatrix(final BufferedReader reader) throws OctaveException {
        this(reader, true);
    }

    /**
     * @param reader
     * @param close
     *            whether to close the stream. Really should be true by default, but Java....
     * @throws OctaveException
     */
    public OctaveNdMatrix(final BufferedReader reader, final boolean close) throws OctaveException {
        try {
            String line;
            // # type: matrix
            line = readerReadLine(reader);
            if (line.equals("# type: scalar")) {
                readScalarMatrix(reader);
            } else if (line.equals("# type: matrix")) {
                // 2d or 2d+?
                line = readerPeekLine(reader);
                if (line.startsWith("# rows: ")) {
                    read2dmatrix(reader);
                } else if (line.startsWith("# ndims: ")) {
                    readVectorizedMatrix(reader);
                } else {
                    throw new OctaveException("Expected <# rows: > or <# ndims: >, but got <" + line + ">");
                }
            } else {
                throw new OctaveException("Wrong type of variable, " + line);
            }
            if (close) {
                reader.close();
            }
        } catch (final IOException e) {
            throw new OctaveException(e);
        }

    }

    private void readScalarMatrix(final BufferedReader reader) throws OctaveException {
        final String line = readerReadLine(reader);
        size = new int[2];
        size[0] = 1;
        size[1] = 1;
        data = new double[1];
        data[0] = parseDouble(line);

    }

    private void readVectorizedMatrix(final BufferedReader reader) throws OctaveException {
        String line;
        final String NDIMS = "# ndims: ";
        line = readerReadLine(reader);
        if (!line.startsWith(NDIMS)) {
            throw new OctaveException("Expected <" + NDIMS + ">, but got <" + line + ">");
        }
        final int ndims = Integer.parseInt(line.substring(NDIMS.length()));
        line = readerReadLine(reader);
        final String[] split = line.substring(1).split(" ");
        if (split.length != ndims) {
            throw new OctaveException("Expected " + ndims + " dimesion, but got " + (split.length) + " (line was <"
                    + line + ">)");
        }
        size = new int[split.length];
        for (int dim = 0; dim < split.length; dim++) {
            size[dim] = Integer.parseInt(split[dim]);
        }
        data = new double[product(size)];
        for (int idx = 0; idx < data.length; idx++) {
            line = readerReadLine(reader);
            data[idx] = parseDouble(line);
        }

    }

    private void read2dmatrix(final BufferedReader reader) throws OctaveException {
        String line;
        // # rows: 1
        line = readerReadLine(reader);
        if (!line.startsWith("# rows: ")) {
            throw new OctaveException("Expected <# rows: > got <" + line + ">");
        }
        final int rows = Integer.valueOf(line.substring(8));
        // # columns: 3
        line = readerReadLine(reader);
        if (!line.startsWith("# columns: ")) {
            throw new OctaveException("Expected <# columns: > got <" + line + ">");
        }
        final int columns = Integer.valueOf(line.substring(11));
        // 1 2 3
        size = new int[2];
        size[0] = rows;
        size[1] = columns;
        data = new double[rows * columns];
        for (int r = 1; r <= rows; ++r) {
            line = readerReadLine(reader);
            final String[] split = line.split(" ");
            if (split.length != columns + 1) {
                throw new OctaveException("Error in matrix-format: '" + line + "'");
            }
            for (int c = 1; c < split.length; c++) {
                set(parseDouble(split[c]), r, c);
            }
        }
    }

    /**
     * @return the data
     */
    public double[] getData() {
        return data;
    }

    /**
     * @return the size
     */
    public int[] getSize() {
        return size;
    }

    /**
     * @param size
     */
    public OctaveNdMatrix(final int... size) {
        init(size);
        data = new double[product(size)];
    }

    /**
     * @param ns
     * @return product of rs
     */
    protected static int product(final int... ns) {
        int p = 1;
        for (final int n : ns) {
            p *= n;
        }
        return p;
    }

    private void init(final int... size_) throws IllegalArgumentException {
        if (size_.length == 0) {
            throw new IllegalArgumentException("no size");
        }
        if (size_.length < 2) {
            throw new IllegalArgumentException("size must have a least 2 dimenstions");
        }
        for (final int s : size_) {
            if (s < 0) {
                throw new IllegalArgumentException("element in size less than zero. =" + s);
            }
        }
        this.size = size_;
    }

    /**
     * @param data
     * @param size
     */
    public OctaveNdMatrix(final double[] data, final int... size) {
        init(size);
        if (product(size) != data.length) {
            final StringBuilder text = new StringBuilder();
            text.append("length of data(");
            text.append(data.length);
            text.append(") doesn't fit with size(");

            text.append("[");
            boolean first = true;
            for (final int i : size) {
                if (first) {
                    first = false;
                } else {
                    text.append(", ");
                }
                text.append(i);
            }
            text.append("]");

            text.append(")");
            throw new IllegalArgumentException(text.toString());
        }
        this.data = data;
    }

    /**
     * @param value
     * @param pos
     *            position in matrix, 1-indexed.
     */
    public void set(final double value, final int... pos) {
        resize(pos);
        data[pos2ind(pos)] = value;
    }

    @Override
    public void save(final String name, final Writer writer) throws IOException {
        writer.write("# name: " + name + "\n# type: matrix\n");
        if (size.length > 2) {
            saveDataVectorized(writer);
        } else {
            saveData2d(writer);
        }
    }

    private void saveData2d(final Writer writer) throws IOException {
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

    private void saveDataVectorized(final Writer writer) throws IOException {
        writer.write("# ndims: " + size.length + "\n");
        for (final int sdim : size) {
            writer.write(" " + sdim);
        }
        for (final double d : data) {
            writer.write("\n " + d);
        }
        writer.write("\n\n");
    }

    @Override
    public OctaveNdMatrix makecopy() {
        return new OctaveNdMatrix(data, size);
    }

    /**
     * @param pos
     * @return the index into data() for the position
     */
    public int pos2ind(final int... pos) {
        int idx = 0;
        int factor = 1;
        for (int dim = 0; dim < pos.length; dim++) {
            if (pos[dim] > size[dim]) {
                throw new IndexOutOfBoundsException("pos exceeded dimension for dimension " + dim + " (" + pos[dim]
                        + " > " + size[dim] + ")");
            }
            idx += (pos[dim] - 1) * factor;
            factor *= size[dim];
        }
        return idx;
    }

    /**
     * @param pos
     * @return value for pos
     */
    public double get(final int... pos) {
        return data[pos2ind(pos)];
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof OctaveNdMatrix) {
            final OctaveNdMatrix matrix = (OctaveNdMatrix) obj;
            return Arrays.equals(matrix.size, size) && Arrays.equals(matrix.data, data);
        } else {
            return false;
        }
    }

    private void resize(final int... pos) {
        if (size.length != pos.length) {
            throw new UnsupportedOperationException("Change in number of dimenstions not supported");
        }
        // Resize from the smallest dimension. This is not the optimal way to do it,
        // but it works.
        int smallest_dim = 0;
        final int[] newsize = new int[size.length];
        System.arraycopy(size, 0, newsize, 0, size.length);
        for (; smallest_dim < size.length; smallest_dim++) {
            if (pos[smallest_dim] > size[smallest_dim]) {
                newsize[smallest_dim] = pos[smallest_dim];
                resizework(smallest_dim, newsize);
            }
        }

    }

    private void resizework(final int smallest_dim, final int[] pos) {
        // Calculate blocksize
        int blocksize = 1;
        for (int dim = 0; dim < smallest_dim + 1; dim++) {
            blocksize *= size[dim];
        }

        // Calculate new dimensions
        final int[] newsize = new int[size.length];
        for (int dim = 0; dim < newsize.length; dim++) {
            newsize[dim] = Math.max(pos[dim], size[dim]);
        }

        // Calculate target stride
        int stride = 1;
        for (int dim = 0; dim < smallest_dim + 1; dim++) {
            stride *= newsize[dim];
        }

        // Allocate new data array if neccessary
        final int neededSize = product(newsize);
        if (data.length < neededSize) {
            final double[] newdata = new double[neededSize * 2];
            // Move data into new array
            int src_offset = 0;
            for (int dest_offset = 0; dest_offset < neededSize; dest_offset += stride) {
                System.arraycopy(data, src_offset, newdata, dest_offset, blocksize);
                src_offset += blocksize;
            }

            // Sanity check
            if (src_offset != product(size)) {
                throw new IllegalStateException("Failed to copy all data in resize");
            }

            // Set data
            data = newdata;

        } else {
            // Move around the data
            int src_offset = product(size) - blocksize;
            int dest_offset = neededSize - stride;
            while (src_offset > 0) {
                System.arraycopy(data, src_offset, data, dest_offset, blocksize);
                Arrays.fill(data, dest_offset + blocksize, dest_offset + stride, 0.0);
                src_offset -= blocksize;
                dest_offset -= stride;
            }
        }

        // set new size
        size = newsize;

    }

    /**
     * @return columns in matrix
     */
    public int columns() {
        return size[1];
    }

    /**
     * @return rows in matrix
     */
    public int rows() {
        return size[0];
    }

}

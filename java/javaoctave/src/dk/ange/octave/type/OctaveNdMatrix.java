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

import java.util.Arrays;

/**
 * @author Kim Hansen
 */
public class OctaveNdMatrix extends OctaveType {

    private static final long serialVersionUID = 4697636627793706177L;

    /**
     * The dimensions, rows x columns x depth x ....
     */
    protected int[] size;

    /**
     * The data, vectorized.
     */
    protected double[] data;

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
        if (product(size) > data.length) {
            final StringBuilder text = new StringBuilder();
            text.append("length of data(");
            text.append(data.length);
            text.append(") is smaller than size(");
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
     *                position in matrix, 1-indexed.
     */
    public void set(final double value, final int... pos) {
        resize(pos);
        data[pos2ind(pos)] = value;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(size);
        result = prime * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OctaveNdMatrix other = (OctaveNdMatrix) obj;
        if (!Arrays.equals(size, other.size)) {
            return false;
        }
        if (!Arrays.equals(data, other.data)) {
            return false;
        }
        return true;
    }

}

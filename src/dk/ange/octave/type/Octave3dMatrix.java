package dk.ange.octave.type;

import java.io.BufferedReader;

import dk.ange.octave.OctaveException;

/**
 * 3d matrix class. It's just a convenience wrapper around OctaveNdMatrix
 */
public class Octave3dMatrix extends OctaveNdMatrix {

    /**
     * @return no of rows in matrix
     */
    public int rows() {
        return size[0];
    }

    /**
     * @return no of rows in matrix
     */
    public int columns() {
        return size[1];
    }

    /**
     * @return no of rows in matrix
     */
    public int depths() {
        return size[2];
    }

    /**
     * @param rows
     * @param columns
     * @param depth
     */
    public Octave3dMatrix(int rows, int columns, int depth) {
        super(rows, columns, depth);
    }

    /**
     * @param data
     * @param rows
     * @param columns
     * @param depth
     */
    public Octave3dMatrix(double[] data, int rows, int columns, int depth) {
        super(data, rows, columns, depth);
    }

    /**
     * @param reader
     * @param close
     *            whether to close the stream. Really should be true by default, but Java....
     * @throws OctaveException
     */
    public Octave3dMatrix(BufferedReader reader, boolean close) throws OctaveException {
        super(reader, close);
    }

    /**
     * @param reader
     * @throws OctaveException
     */
    public Octave3dMatrix(BufferedReader reader) throws OctaveException {
        this(reader, true);
    }

    /**
     * @param value
     * @param row
     * @param column
     * @param depth
     */
    public void set(double value, int row, int column, int depth) {
        if (column > columns())
            throw new IllegalArgumentException("column > columns");
        if (row > rows())
            throw new IllegalArgumentException("row > rows");
        if (depth > depths())
            throw new IllegalArgumentException("row > rows");
        super.set(value, row, column, depth);
    }

    /*
     * (non-Javadoc)
     * @see dk.ange.octave.type.OctaveType#makecopy()
     */
    @Override
    public Octave3dMatrix makecopy() {
        return new Octave3dMatrix(data, rows(), columns(), depths());
    }

    double get(int row, int column, int depth) {
        return super.get(row, column, depth);
    }

}

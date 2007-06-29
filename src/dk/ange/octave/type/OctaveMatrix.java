package dk.ange.octave.type;

import java.io.BufferedReader;

import dk.ange.octave.OctaveException;

/**
 * @author Kim Hansen
 */
public class OctaveMatrix extends OctaveNdMatrix {

    /**
     * 
     */
    public OctaveMatrix() {
        this(0, 0);
    }

    /**
     * @param rows
     * @param columns
     */
    public OctaveMatrix(int rows, int columns) {
        super(rows, columns);
    }

    /**
     * @param data
     * @param rows
     * @param columns
     */
    public OctaveMatrix(double[] data, int rows, int columns) {
        super(data, rows, columns);
    }

    /**
     * @param reader
     * @param close
     *            whether to close the stream.
     * @throws OctaveException
     */
    public OctaveMatrix(BufferedReader reader, boolean close) throws OctaveException {
        super(reader, close);
    }

    /**
     * @param reader
     * @throws OctaveException
     */
    public OctaveMatrix(BufferedReader reader) throws OctaveException {
        this(reader, true);
    }

    /**
     * @param value
     * @param row
     * @param column
     */
    public void set(double value, int row, int column) {
        super.set(value, row, column);
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

    /**
     * @param row
     * @param column
     * @return Returns the value at (row,column)
     */
    public double get(int row, int column) {
        if (row <= 0 || row > rows())
            throw new IndexOutOfBoundsException("row out of range, row=" + row);
        if (column <= 0 || column > columns())
            throw new IndexOutOfBoundsException("column out of range, column=" + column);
        return data[(column - 1) * rows() + row - 1];
    }

    @Override
    public OctaveMatrix makecopy() {
        return new OctaveMatrix(data, rows(), columns());
    }

}

package dk.ange.octave.type;

import java.io.IOException;
import java.io.Writer;

/**
 * 3d matrix class created as a list of 2d matrices
 */
public class Octave3dMatrix extends OctaveType {

    private double[] data;

    private int rows;

    private int columns;

    private int depth;

    public Octave3dMatrix(int rows, int columns, int depth) {
        init(rows, columns, depth);
        data = new double[rows * columns * depth];
    }

    private void init(int rows, int columns, int depth)
            throws IllegalArgumentException {
        if (rows < 0)
            throw new IllegalArgumentException("rows in size less than zero. ="
                    + rows);
        if (columns < 0)
            throw new IllegalArgumentException(
                    "columns in size less than zero. =" + columns);
        if (depth < 0)
            throw new IllegalArgumentException("depth less than zero. ="
                    + depth);
        this.rows = rows;
        this.columns = columns;
        this.depth = depth;
    }

    public Octave3dMatrix(double[] data, int rows, int columns, int depth) {
        init(rows, columns, depth);
        if (rows * columns * depth != data.length)
            throw new IllegalArgumentException(
                    "length of data doesn't fit with size");
        this.data = data;
    }

    public void set(double value, int... pos) {
        // TODO check args
        data[pos[1] + rows] = value;
    }

    @Override
    public void toOctave(Writer writer, String name) throws IOException {
        writer.write(name + "=[\n");
        // for (int r = 0; r < rows; r++) {
        // for (int c = 0; c < columns; c++) {
        // writer.write(Double.toString(data[r * columns + c]));
        // writer.write(' ');
        // }
        // writer.write('\n');
        // }
        writer.write("];\n");
    }

}

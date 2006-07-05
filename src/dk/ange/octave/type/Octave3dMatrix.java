package dk.ange.octave.type;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * 3d matrix class created as a list of 2d matrices
 */
public class Octave3dMatrix extends OctaveType {

    private List<OctaveMatrix> data;

    private int rows;

    private int columns;

    private int depth;

    /**
     * @param rows
     * @param columns
     * @param depth
     */
    public Octave3dMatrix(int rows, int columns, int depth) {
        init(rows, columns, depth);
        data = new ArrayList<OctaveMatrix>(depth);
        for (int i = 1; i <= depth; ++i) {
            data.add(new OctaveMatrix(rows, columns));
        }
    }

    private void init(int rows_, int columns_, int depth_)
            throws IllegalArgumentException {
        if (rows_ < 0)
            throw new IllegalArgumentException("rows in size less than zero. ="
                    + rows_);
        if (columns_ < 0)
            throw new IllegalArgumentException(
                    "columns in size less than zero. =" + columns_);
        if (depth_ < 0)
            throw new IllegalArgumentException("depth less than zero. ="
                    + depth_);
        this.rows = rows_;
        this.columns = columns_;
        this.depth = depth_;
    }

    /**
     * @param value
     * @param row
     * @param column
     * @param depth
     */
    public void set(double value, int row, int column, int depth) {
        if (column > columns)
            throw new IllegalArgumentException("column > columns");
        if (row > rows)
            throw new IllegalArgumentException("row > rows");
        data.get(depth - 1).set(row, column, value);
    }

    @Override
    public void toOctave(Writer writer, String name) throws IOException {
        for (int i = 1; i <= depth; ++i) {
            data.get(i - 1).toOctave(writer, name + "(:,:," + i + ")");
        }
    }

}

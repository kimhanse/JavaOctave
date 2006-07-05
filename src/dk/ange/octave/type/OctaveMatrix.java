package dk.ange.octave.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import dk.ange.octave.OctaveException;

/**
 * @author Kim Hansen
 */
public class OctaveMatrix extends OctaveType {

    private double[] data;

    private int rows;

    private int columns;

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
        init(rows, columns);
        data = new double[rows * columns];
    }

    private void init(int rows_, int columns_) throws IllegalArgumentException {
        this.rows = rows_;
        this.columns = columns_;
        if (rows_ < 0)
            throw new IllegalArgumentException("rows less than zero. rows="
                    + rows_);
        if (columns_ < 0)
            throw new IllegalArgumentException(
                    "columns less than zero. columns=" + columns_);
    }

    /**
     * @param data
     * @param rows
     * @param columns
     */
    public OctaveMatrix(double[] data, int rows, int columns) {
        init(rows, columns);
        this.data = data;
    }

    /**
     * @param reader
     * @throws OctaveException
     */
    public OctaveMatrix(BufferedReader reader) throws OctaveException {
        try {
            String line;
            // # type: matrix
            line = readerReadLine(reader);
            if (!line.equals("# type: matrix"))
                throw new OctaveException("Wrong type of variable");
            // # rows: 1
            line = readerReadLine(reader);
            if (!line.startsWith("# rows: "))
                throw new OctaveException("Expected <# rows: > got <" + line
                        + ">");
            rows = Integer.valueOf(line.substring(8));
            // # columns: 3
            line = readerReadLine(reader);
            if (!line.startsWith("# columns: "))
                throw new OctaveException("Expected <# columns: > got <" + line
                        + ">");
            columns = Integer.valueOf(line.substring(11));
            // 1 2 3
            data = new double[rows * columns];
            for (int r = 1; r <= rows; ++r) {
                line = readerReadLine(reader);
                String[] split = line.split(" ");
                if (split.length != columns + 1)
                    throw new OctaveException("Error in matrix-format: '"
                            + line + "'");
                for (int c = 1; c < split.length; c++) {
                    set(r, c, Double.parseDouble(split[c]));
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new OctaveException(e);
        }
    }

    /**
     * @param row
     * @param column
     * @param value
     */
    public void set(int row, int column, double value) {
        if (row <= 0)
            throw new IndexOutOfBoundsException("row not positive, row=" + row);
        if (column <= 0)
            throw new IndexOutOfBoundsException("column not positive, column="
                    + column);
        if (row > rows || column > columns)
            resize(row, column);
        data[(row - 1) * columns + column - 1] = value;
    }

    /**
     * @param row
     * @param column
     * @return Returns the value at (row,column)
     */
    public double get(int row, int column) {
        if (row <= 0 || row > rows)
            throw new IndexOutOfBoundsException("row out of range, row=" + row);
        if (column <= 0 || column > columns)
            throw new IndexOutOfBoundsException("column out of range, column="
                    + column);
        return data[(row - 1) * columns + column - 1];
    }

    private void resize(int row, int column) {
        int newRows = row > rows ? row : rows;
        int newColumns = column > columns ? column : columns;
        if (newColumns != columns && !(rows == 0 || newRows == 1)) {
            throw new Error("Not implemented! (" + row + " " + rows + " "
                    + column + " " + columns + ")");
            // TODO implement resize(int, int) for newColumns != columns
            // needs to reorder the data during the resize
        }
        rows = newRows;
        columns = newColumns;
        if (rows * columns <= data.length)
            return;
        // Resize the data-array
        double[] oldData = data;
        data = new double[rows * columns * 2];
        System.arraycopy(oldData, 0, data, 0, oldData.length);
    }

    @Override
    public void toOctave(Writer writer, String name) throws IOException {
        if (rows == 0 || columns == 0) {
            writer.write(name + "=zeros(" + rows + "," + columns + ");\n");
        } else {
            writer.write(name + "=[\n");
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    writer.write(' ');
                    writer.write(Double.toString(data[r * columns + c]));
                }
                writer.write('\n');
            }
            writer.write("];\n");
        }
    }

    /**
     * @return Returns the number of rows.
     */
    public int getRowDimension() {
        return rows;
    }

    /**
     * @return Returns the number of columns.
     */
    public int getColumnDimension() {
        return columns;
    }

}

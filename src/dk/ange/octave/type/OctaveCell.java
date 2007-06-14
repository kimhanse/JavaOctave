package dk.ange.octave.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import dk.ange.octave.OctaveException;

/**
 * @author Kim Hansen
 */
public class OctaveCell extends OctaveType {

    private static final OctaveType EMPTY_CELL = new OctaveMatrix(0, 0);

    final private ArrayList<ArrayList<OctaveType>> data;

    private int rows;

    private int columns;

    /**
     * 
     */
    public OctaveCell() {
        data = new ArrayList<ArrayList<OctaveType>>();
        rows = 0;
        columns = 0;
    }

    /**
     * @param value
     */
    public OctaveCell(OctaveType value) {
        this();
        set(1, 1, value);
    }

    /**
     * @param reader
     * @throws OctaveException
     */
    public OctaveCell(BufferedReader reader) throws OctaveException {
        this(reader, true);
    }

    /**
     * @param reader
     * @param close
     *            whether to close the stream. Really should be true by default, but Java....
     * @throws OctaveException
     */
    public OctaveCell(BufferedReader reader, boolean close) throws OctaveException {
        this();
        try {
            String line;
            String token;
            line = readerReadLine(reader);
            token = "# type: cell";
            if (!line.equals(token)) {
                throw new OctaveException("Expected <" + token + ">, but got <" + line + ">");
            }

            line = readerReadLine(reader);
            token = "# rows: ";
            if (!line.startsWith(token)) {
                throw new OctaveException("Expected <" + token + ">, but got <" + line + ">");
            }
            int nrows = Integer.parseInt(line.substring(token.length()));

            line = readerReadLine(reader);
            token = "# columns: ";
            if (!line.startsWith(token)) {
                throw new OctaveException("Expected <" + token + ">, but got <" + line + ">");
            }
            int ncols = Integer.parseInt(line.substring(token.length()));
            for (int col = 1; col <= ncols; col++) {
                for (int row = 1; row <= nrows; row++) {
                    line = readerReadLine(reader);
                    token = "# name: <cell-element>";
                    if (!line.equals(token)) {
                        throw new OctaveException("Expected <" + token + ">, but got <" + line + ">");
                    }
                    OctaveType octaveType = OctaveType.readOctaveType(reader, false);
                    set(row, col, octaveType);
                }
                line = readerReadLine(reader);
                token = "";
                if (!line.equals(token)) {
                    throw new OctaveException("Expected <" + token + ">, but got <" + line + ">");
                }
            }
            if (close) {
                reader.close();
            }

            // Post conditions
            if (rows != data.size()) {
                throw new IllegalStateException("After read, number of rows doesn't match the number read");
            }
            for (ArrayList<OctaveType> row : data) {
                if (columns != row.size()) {
                    throw new IllegalStateException("After read, number of columns doesn't match the number read");
                }
            }
        } catch (IOException e) {
            throw new OctaveException(e);
        }

    }

    /**
     * @param rows
     * @param columns
     */
    public OctaveCell(int rows, int columns) {
        data = new ArrayList<ArrayList<OctaveType>>();
        this.rows = 0;
        this.columns = 0;
        resize(rows, columns);
    }

    private OctaveCell(int rows, int columns, ArrayList<ArrayList<OctaveType>> data) {
        this.rows = rows;
        this.columns = columns;
        this.data = data;

    }

    private void resize(int newRows, int newColumns) {
        while (newRows > rows) {
            ArrayList<OctaveType> newrow = new ArrayList<OctaveType>();
            data.add(newrow);
            for (int i = 0; i < columns; i++) {
                newrow.add(EMPTY_CELL);
            }
            rows++;
        }
        while (newColumns > columns) {
            for (ArrayList<OctaveType> rowData : data) {
                rowData.add(EMPTY_CELL);
            }
            columns++;
        }
    }

    /**
     * @param row
     * @param column
     * @return (shallow copyof ) value for row and column. Empty cells are 0x0 matrixes.
     */
    public OctaveType get(final int row, final int column) {
        if (row < 1 || row > rows) {
            throw new IndexOutOfBoundsException("row was " + row + " and must be between 1 and " + rows);
        }
        if (column < 1 || column > columns) {
            throw new IndexOutOfBoundsException("column was " + column + " and must be between 1 and " + columns);
        }
        return OctaveType.copy(data.get(row - 1).get(column - 1));
    }

    /**
     * @param row
     * @param column
     * @param value
     */
    public void set(final int row, final int column, final OctaveType value) {
        if (row < 1) {
            throw new IllegalArgumentException("row cannot be less or equal to 0");
        }
        if (column < 1) {
            throw new IllegalArgumentException("column cannot be less or equal to 0");
        }

        // Expand if needed
        resize(Math.max(row, rows), Math.max(column, columns));

        // Finally, set value
        data.get(row - 1).set(column - 1, value);
    }

    /**
     * @return Returns number or rows.
     */
    public int getRowDimension() {
        return rows;
    }

    /**
     * @return Returns number of columns
     */
    public int getColumnDimension() {
        return columns;
    }

    @Override
    public void save(String name, Writer writer) throws IOException {
        writer.write("# name: " + name + "\n# type: cell\n# rows: " + rows + "\n# columns: " + columns + "\n");
        for (ArrayList<OctaveType> row : data) {
            for (OctaveType value : row) {
                value.save("<cell-element>", writer);
            }
            writer.write("\n");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see dk.ange.octave.type.OctaveType#makecopy()
     */
    @Override
    public OctaveCell makecopy() {
        return new OctaveCell(rows, columns, data);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OctaveCell) {
            OctaveCell cell = (OctaveCell) obj;
            if (cell.rows != rows || cell.columns != columns) {
                return false;
            }
            for (int row=0; row<rows; row++) {
                ArrayList<OctaveType> cellrow = cell.data.get(row);
                ArrayList<OctaveType> thisrow = data.get(row);
                for (int col=0; col<columns; col++) {
                    OctaveType thisvalue = thisrow.get(col);
                    OctaveType cellvalue = cellrow.get(col);
                    if (thisvalue != null) {
                        if (!thisvalue.equals(cellvalue)) {
                            return false;
                        }
                    } else {
                        if (cellvalue != null) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }
    
    

}

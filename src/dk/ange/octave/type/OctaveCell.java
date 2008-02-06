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
import java.util.ArrayList;

import dk.ange.octave.OctaveException;

/**
 * @author Kim Hansen
 */
public class OctaveCell extends OctaveType {

    private static final long serialVersionUID = -8884907460912911699L;

    private static final OctaveType EMPTY_CELL = new OctaveNdMatrix(0, 0);

    private final ArrayList<ArrayList<OctaveType>> data;

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
    public OctaveCell(final OctaveType value) {
        this();
        set(1, 1, value);
    }

    /**
     * @param reader
     * @throws OctaveException
     */
    public OctaveCell(final BufferedReader reader) throws OctaveException {
        this(reader, true);
    }

    /**
     * @param reader
     * @param close
     *                whether to close the stream. Really should be true by default, but Java....
     * @throws OctaveException
     */
    public OctaveCell(final BufferedReader reader, final boolean close) throws OctaveException {
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
            final int nrows = Integer.parseInt(line.substring(token.length()));

            line = readerReadLine(reader);
            token = "# columns: ";
            if (!line.startsWith(token)) {
                throw new OctaveException("Expected <" + token + ">, but got <" + line + ">");
            }
            final int ncols = Integer.parseInt(line.substring(token.length()));
            for (int col = 1; col <= ncols; col++) {
                for (int row = 1; row <= nrows; row++) {
                    line = readerReadLine(reader);
                    token = "# name: <cell-element>";
                    if (!line.equals(token)) {
                        throw new OctaveException("Expected <" + token + ">, but got <" + line + ">");
                    }
                    final OctaveType octaveType = OctaveType.readOctaveType(reader, false);
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
            for (final ArrayList<OctaveType> row : data) {
                if (columns != row.size()) {
                    throw new IllegalStateException("After read, number of columns doesn't match the number read");
                }
            }
        } catch (final IOException e) {
            throw new OctaveException(e);
        }
    }

    /**
     * @param rows
     * @param columns
     */
    public OctaveCell(final int rows, final int columns) {
        data = new ArrayList<ArrayList<OctaveType>>();
        this.rows = 0;
        this.columns = 0;
        resize(rows, columns);
    }

    private OctaveCell(final int rows, final int columns, final ArrayList<ArrayList<OctaveType>> data) {
        this.rows = rows;
        this.columns = columns;
        this.data = data;
    }

    private void resize(final int newRows, final int newColumns) {
        while (newRows > rows) {
            final ArrayList<OctaveType> newrow = new ArrayList<OctaveType>();
            data.add(newrow);
            for (int i = 0; i < columns; i++) {
                newrow.add(EMPTY_CELL);
            }
            rows++;
        }
        while (newColumns > columns) {
            for (final ArrayList<OctaveType> rowData : data) {
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
    public void save(final String name, final Writer writer) throws IOException {
        writer.write("# name: " + name + "\n# type: cell\n# rows: " + rows + "\n# columns: " + columns + "\n");
        for (int c = 0; c < columns; ++c) {
            for (int r = 0; r < rows; ++r) {
                data.get(r).get(c).save("<cell-element>", writer);
            }
            writer.write("\n");
        }
    }

    @Override
    public OctaveCell makecopy() {
        return new OctaveCell(rows, columns, data);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof OctaveCell) {
            final OctaveCell cell = (OctaveCell) obj;
            if (cell.rows != rows || cell.columns != columns) {
                return false;
            }
            for (int row = 0; row < rows; row++) {
                final ArrayList<OctaveType> cellrow = cell.data.get(row);
                final ArrayList<OctaveType> thisrow = data.get(row);
                for (int col = 0; col < columns; col++) {
                    final OctaveType thisvalue = thisrow.get(col);
                    final OctaveType cellvalue = cellrow.get(col);
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

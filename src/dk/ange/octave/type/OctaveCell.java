package dk.ange.octave.type;

import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import dk.ange.octave.OctaveException;

public class OctaveCell extends OctaveType {

    private Vector<Vector<OctaveType>> data;

    private int rows;

    private int columns;

    public OctaveCell() {
        data = new Vector<Vector<OctaveType>>();
        rows = 0;
        columns = 0;
    }

    public OctaveCell(OctaveType value) {
        data = new Vector<Vector<OctaveType>>();
        data.add(new Vector<OctaveType>());
        rows = 1;
        data.get(0).add(value);
        columns = 1;
    }

    public OctaveCell(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        data = new Vector<Vector<OctaveType>>();
        for (int r = 1; r <= rows; ++r)
            data.add(new Vector<OctaveType>());
    }

    // TODO Not implemented
    @SuppressWarnings("unused")
    private void set(int place, OctaveType value) {
        // TODO This should have special actions if columns = 0 or 1
        if (place < 1 || place > rows * columns)
            throw new IndexOutOfBoundsException();
        // place-1 = rows*(col-1) + row-1
        // row-1 = (place-1) mod rows
        // col-1 = (place-1) div rows
        int row = ((place - 1) % rows) + 1;
        int column = ((place - 1) / rows) + 1;
        // TODO OctaveCell.set(int place, OctaveType value) is not implemented
        throw new Error("Not implemented! " + value + row + column);
    }

    public void set(int row, int column, OctaveType value) {
        // TODO: Check row, column > 0
        if (row > rows) {
            for (int newrow = rows + 1; newrow <= row; ++newrow) {
                data.add(new Vector<OctaveType>());
            }
            rows = row;
        }
        if (column > columns)
            columns = column;
        Vector<OctaveType> rowData = data.get(row - 1);
        if (column > rowData.size())
            rowData.setSize(column);
        rowData.set(column - 1, value);
    }

    @Override
    public void toOctave(Writer writer, String name) throws OctaveException {
        try {
            // FIXME This will break with nested cells
            String tmp_var_name = "octave_java_tmp_cell";
            boolean tmp_var_used = false;
            writer.write(name + "=cell(" + rows + ',' + columns + ");\n");
            for (int r = 1; r <= rows; ++r) {
                Vector<OctaveType> row = data.get(r - 1);
                for (int c = 1; c <= row.size(); c++) {
                    OctaveType d = row.get(c - 1);
                    if (d == null)
                        continue;
                    writer.write("clear " + tmp_var_name + ";\n");
                    d.toOctave(writer, tmp_var_name);
                    tmp_var_used = true;
                    writer.write(name + '{' + r + ',' + c + "}=" + tmp_var_name
                            + ";\n");
                }
            }
            if (tmp_var_used) {
                writer.write("clear " + tmp_var_name + ";\n");
            }
        } catch (IOException e) {
            throw new OctaveException(e);
        }
    }

    public int getRowDimension() {
        return rows;
    }

    public int getColumnDimension() {
        return columns;
    }

}

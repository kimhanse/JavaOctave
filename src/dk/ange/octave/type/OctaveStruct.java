package dk.ange.octave.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import dk.ange.octave.OctaveException;

/**
 * @author Kim Hansen
 * @author Esben Mose Hansen
 */
public class OctaveStruct extends OctaveType {

    private static final long serialVersionUID = 430390185317050230L;

    private Map<String, OctaveType> data = new HashMap<String, OctaveType>();

    /**
     */
    public OctaveStruct() {
        // Stupid warning suppression
    }

    /**
     * @param reader
     * @throws OctaveException
     */
    public OctaveStruct(final BufferedReader reader) throws OctaveException {
        this(reader, true);
    }

    /**
     * @param reader
     * @param close
     *            whether to close the stream. Really should be true by default, but Java....
     * @throws OctaveException
     */
    public OctaveStruct(final BufferedReader reader, final boolean close) throws OctaveException {
        try {
            String line;
            final String TYPE_STRUCT = "# type: struct";
            final String TYPE_GLOBAL_STRUCT = "# type: global struct";
            line = readerReadLine(reader);
            if (!line.equals(TYPE_STRUCT) && !line.equals(TYPE_GLOBAL_STRUCT)) {
                throw new OctaveException("Variable was not a struct or global struct, " + line);
            }
            // # length: 4
            line = readerReadLine(reader);

            final String LENGTH = "# length: ";
            if (!line.startsWith(LENGTH)) {
                throw new OctaveException("Expected <" + LENGTH + "> got <" + line + ">");
            }
            final int length = Integer.valueOf(line.substring(LENGTH.length())); // only used during conversion

            for (int i = 0; i < length; i++) {
                // # name: elemmatrix
                final String NAME = "# name: ";
                line = readerReadLine(reader);
                if (!line.startsWith(NAME)) {
                    throw new OctaveException("Expected <" + NAME + "> got <" + line + ">");
                }
                final String subname = line.substring(NAME.length());
                final OctaveCell cell = new OctaveCell(reader, false);
                // If the cell is a 1x1, move up the value
                if (cell.getRowDimension() == 1 && cell.getColumnDimension() == 1) {
                    final OctaveType value = cell.get(1, 1);
                    data.put(subname, value);
                } else {
                    data.put(subname, cell);
                }
            }
            if (close) {
                reader.close();
            }
        } catch (final IOException e) {
            throw new OctaveException(e);
        }
    }

    private OctaveStruct(final Map<String, OctaveType> data) {
        this.data = data;
    }

    /**
     * @param name
     * @param value
     */
    public void set(final String name, final OctaveType value) {
        data.put(name, value);
    }

    @Override
    public void save(final String name, final Writer writer) throws IOException {
        writer.write("# name: " + name + "\n# type: struct\n# length: " + data.size() + "\n");
        for (final Map.Entry<String, OctaveType> entry : data.entrySet()) {
            final String subname = entry.getKey();
            final OctaveType value = entry.getValue();
            writer.write("# name: " + subname + "\n# type: cell\n# rows: 1\n# columns: 1\n");
            value.save("<cell-element>", writer);
        }

    }

    /**
     * @param key
     * @return (shallow copy of) value for this key, or null if key isn't there.
     */
    public OctaveType get(final String key) {
        return OctaveType.copy(data.get(key));
    }

    @Override
    public OctaveStruct makecopy() {
        return new OctaveStruct(data);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof OctaveStruct) {
            final OctaveStruct struct = (OctaveStruct) obj;
            return data.equals(struct.data);

        }
        return false;
    }

}

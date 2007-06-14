package dk.ange.octave.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import dk.ange.octave.OctaveException;

/**
 * @author kim
 * 
 * http://www.octave.org/mailing-lists/octave-maintainers/2005/258
 * http://www.octave.org/octave-lists/archive/octave-maintainers.2005/msg00280.html
 */
public class OctaveString extends OctaveType {

    String value;

    /**
     * @param reader
     * @throws OctaveException
     */
    public OctaveString(BufferedReader reader) throws OctaveException {
        this(reader, true);
    }

    /**
     * @param reader
     * @param close
     *            whether to close the stream. Really should be true by default, but Java....
     * @throws OctaveException
     */
    public OctaveString(BufferedReader reader, boolean close) throws OctaveException {
        try {
            String line;
            line = readerReadLine(reader);
            if (!line.equals("# type: string"))
                throw new OctaveException("Wrong type of variable");
            line = readerReadLine(reader);
            if (!line.equals("# elements: 1"))
                throw new OctaveException("Only implementet for single-line strings '" + line + "'");
            line = readerReadLine(reader);
            if (!line.startsWith("# length: ")) // TODO use length for checking
                throw new OctaveException("Parse error in String");
            value = readerReadLine(reader);

            if (close) {
                reader.close();
            }
        } catch (IOException e) {
            throw new OctaveException(e);
        }
    }

    /**
     * @param value
     */
    public OctaveString(String value) {
        this.value = value;
    }

    @Override
    public void save(String name, Writer writer) throws IOException {
        writer.write("# name: " + name + "\n# type: string\n# elements: 1\n# length: " + value.length() + "\n" + value
                + "\n\n");
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof OctaveString)) {
            return false;
        }
        OctaveString that = (OctaveString) thatObject;
        return this.value.equals(that.value);
    }

    @Override
    public OctaveString makecopy() {
        return new OctaveString(value);
    }

}

package dk.ange.octave.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import dk.ange.octave.OctaveException;

/**
 * @author Kim Hansen
 */
@Deprecated
public class OctaveSqString extends OctaveType {

    String value;

    /**
     * @param reader
     * @throws OctaveException
     */
    @Deprecated
    public OctaveSqString(BufferedReader reader) throws OctaveException {
        String line;
        line = readerReadLine(reader);
        if (!line.equals("# type: sq_string"))
            throw new OctaveException("Wrong type of variable");
        line = readerReadLine(reader);
        if (!line.equals("# elements: 1"))
            throw new OctaveException(
                    "Only implementet for single-line strings '" + line + "'");
        line = readerReadLine(reader);
        if (!line.startsWith("# length: ")) // TODO use length for checking
            throw new OctaveException("Parse error in SqString");
        value = readerReadLine(reader);
    }

    /**
     * @Deprecated Use OctaveString instead of this class
     * 
     * @param value
     */
    @Deprecated
    public OctaveSqString(String value) {
        this.value = value;
    }

    @Override
    @Deprecated
    public void toOctave(Writer writer, String name) throws IOException {
        writer.write(name + "='" + value + "';\n");
    }

    @Override
    @Deprecated
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof OctaveSqString)) {
            return false;
        }
        OctaveSqString that = (OctaveSqString) thatObject;
        return this.value.equals(that.value);
    }

}

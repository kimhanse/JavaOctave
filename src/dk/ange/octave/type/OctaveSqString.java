package dk.ange.octave.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import dk.ange.octave.OctaveException;

public class OctaveSqString extends OctaveType {

    String value;

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

    public OctaveSqString(String value) {
        this.value = value;
    }

    @Override
    public void toOctave(Writer writer, String name) throws OctaveException {
        try {
            writer.write(name + "='" + value + "';\n");
        } catch (IOException e) {
            throw new OctaveException(e);
        }
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof OctaveSqString)) {
            return false;
        }
        OctaveSqString that = (OctaveSqString) thatObject;
        return this.value.equals(that.value);
    }

}

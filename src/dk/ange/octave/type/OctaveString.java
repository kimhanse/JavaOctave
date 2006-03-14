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

    public OctaveString(BufferedReader reader) throws OctaveException {
        String line;
        line = readerReadLine(reader);
        if (!line.equals("# type: string"))
            throw new OctaveException("Wrong type of variable");
        line = readerReadLine(reader);
        if (!line.equals("# elements: 1"))
            throw new OctaveException(
                    "Only implementet for single-line strings '" + line + "'");
        line = readerReadLine(reader);
        if (!line.startsWith("# length: ")) // TODO use length for checking
            throw new OctaveException("Parse error in String");
        value = readerReadLine(reader);
    }

    public OctaveString(String value) {
        this.value = value;
    }

    @Override
    public void toOctave(Writer writer, String name) throws OctaveException {
        try {
            writer.write(name + "=\"" + value + "\";\n");
        } catch (IOException e) {
            throw new OctaveException(e);
        }
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof OctaveString)) {
            return false;
        }
        OctaveString that = (OctaveString) thatObject;
        return this.value.equals(that.value);
    }

}

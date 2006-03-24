package dk.ange.octave.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import dk.ange.octave.OctaveException;

public class OctaveScalar extends OctaveType {

    double value;

    public OctaveScalar(BufferedReader reader) throws OctaveException {
        try {
            String line = reader.readLine();
            if (!line.equals("# type: scalar"))
                throw new OctaveException("Wrong type of variable");
            line = reader.readLine();
            value = Double.parseDouble(line);
            reader.close();
        } catch (IOException e) {
            throw new OctaveException(e);
        }
    }

    public OctaveScalar(double value) {
        this.value = value;
    }

    @Override
    public void toOctave(Writer writer, String name) throws IOException {
        writer.write(name + '=' + Double.toString(value) + ";\n");
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof OctaveScalar)) {
            return false;
        }
        OctaveScalar that = (OctaveScalar) thatObject;
        return this.value == that.value;
    }

    public double getDouble() {
        return value;
    }

}

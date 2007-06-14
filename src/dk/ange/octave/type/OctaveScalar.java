package dk.ange.octave.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import dk.ange.octave.OctaveException;

/**
 * @author Kim Hansen
 */
public class OctaveScalar extends OctaveType {

    double value;

    /**
     * @param reader
     * @throws OctaveException
     */
    public OctaveScalar(BufferedReader reader) throws OctaveException {
        this(reader, true);
    }

    /**
     * @param reader
     * @param close
     *            whether to close the stream. Really should be true by default, but Java....
     * @throws OctaveException
     */
    public OctaveScalar(BufferedReader reader, boolean close) throws OctaveException {
        try {
            String line = reader.readLine();
            String token = "# type: scalar";
            if (!line.equals(token)) {
                throw new OctaveException("Expected <" + token + ">, but got <" + line + ">\n");
            }
            line = reader.readLine();
            value = parseDouble(line);
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
    public OctaveScalar(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof OctaveScalar)) {
            return false;
        }
        OctaveScalar that = (OctaveScalar) thatObject;
        return this.value == that.value;
    }

    /**
     * @return Returns the value of this object
     */
    public double getDouble() {
        return value;
    }

    @Override
    public void save(String name, Writer writer) throws IOException {
        writer.write("# name: " + name + "\n# type: scalar\n" + value + "\n\n");
    }

    @Override
    public OctaveScalar makecopy() {
        return new OctaveScalar(value);
    }

    /**
     * Sets value
     * 
     * @param value
     */
    public void set(double value) {
        this.value = value;
    }

}

/**
 * NOT DONE YET, use Octave3dMatrix
 */
package dk.ange.octave.type;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Kim Hansen
 */
public class OctaveNdMatrix extends OctaveType {

    private double[] data;

    private int[] size;

    /**
     * @param size
     */
    public OctaveNdMatrix(int... size) {
        init(size);
        data = new double[product(size)];
    }

    static int product(int... ns) {
        int p = 1;
        for (int n : ns)
            p *= n;
        return p;
    }

    private void init(int... size_) throws IllegalArgumentException {
        if (size_.length == 0)
            throw new IllegalArgumentException("no size");
        for (int s : size_) {
            if (s < 0)
                throw new IllegalArgumentException(
                        "element in size less than zero. =" + s);
        }
        this.size = size_;
        // FIXME NOT IMPLEMENTED
        throw new IllegalArgumentException("NOT IMPLEMENTED");
    }

    /**
     * @param data
     * @param size
     */
    public OctaveNdMatrix(double[] data, int... size) {
        init(size);
        if (product(size) != data.length)
            throw new IllegalArgumentException(
                    "length of data doesn't fit with size");
        this.data = data;
    }

    /**
     * @param value
     * @param pos
     */
    public void set(double value, int... pos) {
        // TODO check args
        data[pos[1] + size[0]] = value;
    }

    @Override
    public void toOctave(Writer writer, String name) throws IOException {
        writer.write(name + "=[\n");
        // for (int r = 0; r < rows; r++) {
        // for (int c = 0; c < columns; c++) {
        // writer.write(Double.toString(data[r * columns + c]));
        // writer.write(' ');
        // }
        // writer.write('\n');
        // }
        writer.write("];\n");
    }

}

package dk.ange.octave.type;

import junit.framework.Assert;
import junit.framework.TestCase;
import dk.ange.octave.Octave;

public class TestOctave3dMatrix extends TestCase {

    public void testConstructor() throws Exception {
        Octave3dMatrix matrix = new Octave3dMatrix(0, 0, 0);
        Assert.assertEquals("", matrix.toOctave("matrix3d"));
    }

    public void testConstructorIntIntInt() throws Exception {
        Octave3dMatrix matrix = new Octave3dMatrix(3, 4, 2);
        Assert.assertEquals("" //
                + "matrix3d(:,:,1)=[\n" //
                + "0.0 0.0 0.0 0.0 \n" // 
                + "0.0 0.0 0.0 0.0 \n" //
                + "0.0 0.0 0.0 0.0 \n" //
                + "];\n" //
                + "matrix3d(:,:,2)=[\n" //
                + "0.0 0.0 0.0 0.0 \n" //
                + "0.0 0.0 0.0 0.0 \n" //
                + "0.0 0.0 0.0 0.0 \n" //
                + "];\n"//
                + "", matrix.toOctave("matrix3d"));
        matrix.set(42.0, 1, 3, 2);
        Assert.assertEquals("" //
                + "matrix3d(:,:,1)=[\n" //
                + "0.0 0.0 0.0 0.0 \n" // 
                + "0.0 0.0 0.0 0.0 \n" //
                + "0.0 0.0 0.0 0.0 \n" //
                + "];\n" //
                + "matrix3d(:,:,2)=[\n" //
                + "0.0 0.0 42.0 0.0 \n" //
                + "0.0 0.0 0.0 0.0 \n" //
                + "0.0 0.0 0.0 0.0 \n" //
                + "];\n"//
                + "", matrix.toOctave("matrix3d"));
    }

    public void testOctave() throws Exception {
        Octave3dMatrix matrix3d = new Octave3dMatrix(3, 4, 2);
        matrix3d.set(42.0, 1, 3, 2);
        matrix3d.set(-1.0, 3, 1, 1);
        Octave octave = new Octave();
        octave.set("matrix3d", matrix3d);
        octave.execute("x1 = matrix3d(:,:,1);");
        octave.execute("x2 = matrix3d(:,:,2);");
        OctaveMatrix x1 = new OctaveMatrix(octave.get("x1"));
        OctaveMatrix x2 = new OctaveMatrix(octave.get("x2"));
        assertEquals(0.0, x1.get(1, 3));
        assertEquals(-1.0, x1.get(3, 1));
        assertEquals(42.0, x2.get(1, 3));
        assertEquals(0.0, x2.get(3, 1));
        octave.close();
    }
}

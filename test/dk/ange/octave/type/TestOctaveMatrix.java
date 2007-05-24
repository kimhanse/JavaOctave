package dk.ange.octave.type;

import java.io.OutputStreamWriter;
import java.io.StringWriter;

import junit.framework.Assert;
import junit.framework.TestCase;
import dk.ange.octave.Octave;

/**
 * @author Kim Hansen
 */
public class TestOctaveMatrix extends TestCase {

    /**
     * @throws Exception
     */
    public void testConstructor() throws Exception {
        OctaveMatrix matrix = new OctaveMatrix();
        Assert.assertEquals(0, matrix.getRowDimension());
        Assert.assertEquals(0, matrix.getColumnDimension());
        Assert.assertEquals("matrix=zeros(0,0);\n", matrix.toOctave("matrix"));
    }

    /**
     * @throws Exception
     */
    public void testConstructorMatrix() throws Exception {
        double[] numbers = { 1, 2, 3, 4, 5, 6 };
        OctaveMatrix matrix = new OctaveMatrix(numbers, 2, 3);
        Assert.assertEquals(2, matrix.getRowDimension());
        Assert.assertEquals(3, matrix.getColumnDimension());
        Assert.assertEquals("matrix=[\n 1.0 2.0 3.0\n 4.0 5.0 6.0\n];\n", matrix.toOctave("matrix"));
    }

    /**
     * @throws Exception
     */
    public void testConstructorIntInt() throws Exception {
        OctaveMatrix matrix = new OctaveMatrix(2, 3);
        Assert.assertEquals(2, matrix.getRowDimension());
        Assert.assertEquals(3, matrix.getColumnDimension());
        Assert.assertEquals("matrix=[\n 0.0 0.0 0.0\n 0.0 0.0 0.0\n];\n", matrix.toOctave("matrix"));
        matrix.set(1, 2, 42);
        Assert.assertEquals("matrix=[\n 0.0 42.0 0.0\n 0.0 0.0 0.0\n];\n", matrix.toOctave("matrix"));
    }

    /**
     * @throws Exception
     */
    public void testGrowth() throws Exception {
        OctaveMatrix matrix;

        matrix = new OctaveMatrix(0, 0);
        Assert.assertEquals(0, matrix.getRowDimension());
        Assert.assertEquals(0, matrix.getColumnDimension());
        Assert.assertEquals("matrix=zeros(0,0);\n"//
                + "", matrix.toOctave("matrix"));
        matrix.set(1, 1, 1);
        Assert.assertEquals(1, matrix.getRowDimension());
        Assert.assertEquals(1, matrix.getColumnDimension());
        Assert.assertEquals("matrix=[\n"//
                + " 1.0\n"//
                + "];\n"//
                + "", matrix.toOctave("matrix"));
        matrix.set(3, 1, 3);
        Assert.assertEquals(3, matrix.getRowDimension());
        Assert.assertEquals(1, matrix.getColumnDimension());
        Assert.assertEquals("matrix=[\n"//
                + " 1.0\n"//
                + " 0.0\n"//
                + " 3.0\n"//
                + "];\n"//
                + "", matrix.toOctave("matrix"));

        matrix = new OctaveMatrix(0, 0);
        matrix.set(1, 3, 3);
        Assert.assertEquals("matrix=[\n"//
                + " 0.0 0.0 3.0\n"//
                + "];\n"//
                + "", matrix.toOctave("matrix"));
    }

    /**
     * @throws Exception
     */
    public void testOctaveGet() throws Exception {
        Octave octave = new Octave();
        octave.execute("m=[1 2;3 4];");
        OctaveMatrix matrix = new OctaveMatrix(octave.get("m"));
        matrix = new OctaveMatrix(octave.get("m"));
        Assert.assertEquals("matrix=[\n"//
                + " 1.0 2.0\n"//
                + " 3.0 4.0\n"//
                + "];\n"//
                + "", matrix.toOctave("matrix"));
    }

    /**
     * @throws Exception
     */
    public void testOctaveSetExecGet() throws Exception {
        double[] numbers = { 1, 2, 3, 4, 5, 6 };
        Octave octave = new Octave();
        OctaveMatrix in = new OctaveMatrix(numbers, 2, 3);
        octave.set("in", in);
        octave.execute("out=in;");
        OctaveMatrix out = new OctaveMatrix(octave.get("out"));
        Assert.assertEquals(in.toString(), out.toString());
    }

    /**
     * @throws Exception
     */
    public void testPerformance() throws Exception {
        OctaveMatrix matrix = new OctaveMatrix(0, 30);
        long t = System.currentTimeMillis();
        for (int pos = 1; pos <= 4125; ++pos) {
            matrix.set(pos, 1, 4.2);
            matrix.set(pos, 30, 4.2);
        }
        assertTrue("Performance test didn't finish in 100ms", System.currentTimeMillis() - t < 100);
    }

    /**
     * Test how the system handles save of Inf and NaN
     * 
     * @throws Exception
     */
    public void testSaveNanInf() throws Exception {
        StringWriter stderr = new StringWriter();
        Octave octave = new Octave(null, new OutputStreamWriter(System.out), stderr);
        octave.execute("ok=1;");
        octave.execute("xnan=[NaN 0];");
        new OctaveScalar(octave.get("ok"));
        OctaveMatrix xnan = new OctaveMatrix(octave.get("xnan"));
        assertEquals(Double.NaN, xnan.get(1, 1));
        assertEquals(Double.valueOf(0), xnan.get(1, 2));
        new OctaveScalar(octave.get("ok"));
        octave.execute("xinf=[Inf -Inf];");
        new OctaveScalar(octave.get("ok"));
        OctaveMatrix xinf = new OctaveMatrix(octave.get("xinf"));
        assertEquals(Double.POSITIVE_INFINITY, xinf.get(1, 1));
        assertEquals(Double.NEGATIVE_INFINITY, xinf.get(1, 2));
        new OctaveScalar(octave.get("ok"));
        octave.close();
        stderr.close();
        assertEquals("", stderr.toString()); // No warning when saving matrix with NaN/Inf
    }

}

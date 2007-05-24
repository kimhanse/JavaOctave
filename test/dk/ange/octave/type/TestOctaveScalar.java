package dk.ange.octave.type;

import java.io.OutputStreamWriter;
import java.io.StringWriter;

import junit.framework.Assert;
import junit.framework.TestCase;
import dk.ange.octave.Octave;

/**
 * @author Kim Hansen
 */
public class TestOctaveScalar extends TestCase {

    /**
     * @throws Exception
     */
    public void testToString() throws Exception {
        OctaveType integer = new OctaveScalar(42);
        Assert.assertEquals(integer.toString(), "ans=42.0;\n");
    }

    /**
     * @throws Exception
     */
    public void testToOctave() throws Exception {
        OctaveType integer = new OctaveScalar(43);
        Assert.assertEquals(integer.toOctave("tre"), "tre=43.0;\n");
    }

    /**
     * @throws Exception
     */
    public void testOctaveConnection() throws Exception {
        OctaveType i1 = new OctaveScalar(42);
        Octave octave = new Octave();
        octave.set("i", i1);
        OctaveScalar i2 = new OctaveScalar(octave.get("i"));
        Assert.assertEquals(i1, i2);
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

        octave.execute("xnan=NaN;");
        new OctaveScalar(octave.get("ok"));
        OctaveScalar xnan = new OctaveScalar(octave.get("xnan"));
        assertEquals(Double.NaN, xnan.getDouble());
        new OctaveScalar(octave.get("ok"));

        octave.execute("xinf=Inf;");
        new OctaveScalar(octave.get("ok"));
        OctaveScalar xinf = new OctaveScalar(octave.get("xinf"));
        assertEquals(Double.POSITIVE_INFINITY, xinf.getDouble());
        new OctaveScalar(octave.get("ok"));

        octave.execute("xninf=-Inf;");
        new OctaveScalar(octave.get("ok"));
        OctaveScalar xninf = new OctaveScalar(octave.get("xninf"));
        assertEquals(Double.NEGATIVE_INFINITY, xninf.getDouble());
        new OctaveScalar(octave.get("ok"));

        octave.close();
        stderr.close();
        assertEquals("warning: save: Inf or NaN values may not be reloadable\n"
                + "warning: save: Inf or NaN values may not be reloadable\n"
                + "warning: save: Inf or NaN values may not be reloadable\n", stderr.toString());
    }

}

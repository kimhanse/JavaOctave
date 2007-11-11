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
        final OctaveType integer = new OctaveScalar(42);
        Assert.assertEquals(integer.toString(), "# name: ans\n# type: scalar\n42.0\n\n");
    }

    /**
     * @throws Exception
     */
    public void testToOctave() throws Exception {
        final OctaveType integer = new OctaveScalar(43);
        Assert.assertEquals(integer.toText("tre"), "# name: tre\n# type: scalar\n43.0\n\n");
    }

    /**
     * @throws Exception
     */
    public void testOctaveConnection() throws Exception {
        final OctaveType i1 = new OctaveScalar(42);
        final Octave octave = new Octave();
        octave.set("i", i1);
        final OctaveScalar i2 = new OctaveScalar(octave.get("i"));
        Assert.assertEquals(i1, i2);
    }

    /**
     * Test how the system handles save of Inf and NaN
     * 
     * @throws Exception
     */
    public void testSaveNanInf() throws Exception {
        final StringWriter stderr = new StringWriter();
        final Octave octave = new Octave(null, new OutputStreamWriter(System.out), stderr);
        octave.execute("ok=1;");

        octave.execute("xnan=NaN;");
        new OctaveScalar(octave.get("ok"));
        final OctaveScalar xnan = new OctaveScalar(octave.get("xnan"));
        assertEquals(Double.NaN, xnan.getDouble());
        new OctaveScalar(octave.get("ok"));

        octave.execute("xinf=Inf;");
        new OctaveScalar(octave.get("ok"));
        final OctaveScalar xinf = new OctaveScalar(octave.get("xinf"));
        assertEquals(Double.POSITIVE_INFINITY, xinf.getDouble());
        new OctaveScalar(octave.get("ok"));

        octave.execute("xninf=-Inf;");
        new OctaveScalar(octave.get("ok"));
        final OctaveScalar xninf = new OctaveScalar(octave.get("xninf"));
        assertEquals(Double.NEGATIVE_INFINITY, xninf.getDouble());
        new OctaveScalar(octave.get("ok"));

        octave.close();
        stderr.close();
        assertEquals("", stderr.toString());
    }

}

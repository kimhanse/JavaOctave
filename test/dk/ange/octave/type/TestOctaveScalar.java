package dk.ange.octave.type;

import junit.framework.Assert;
import junit.framework.TestCase;
import dk.ange.octave.Octave;

public class TestOctaveScalar extends TestCase {

    public void testToString() throws Exception {
        OctaveType integer = new OctaveScalar(42);
        Assert.assertEquals(integer.toString(), "ans=42.0;\n");
    }

    public void testToOctave() throws Exception {
        OctaveType integer = new OctaveScalar(43);
        Assert.assertEquals(integer.toOctave("tre"), "tre=43.0;\n");
    }

    public void testOctaveConnection() throws Exception {
        OctaveType i1 = new OctaveScalar(42);
        Octave octave = new Octave();
        octave.set("i", i1);
        OctaveScalar i2 = new OctaveScalar(octave.get("i"));
        Assert.assertEquals(i1, i2);
    }

}

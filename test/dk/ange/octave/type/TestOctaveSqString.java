package dk.ange.octave.type;

import junit.framework.Assert;
import junit.framework.TestCase;
import dk.ange.octave.Octave;

public class TestOctaveSqString extends TestCase {

    public void testToString() throws Exception {
        OctaveType string = new OctaveSqString("tekst");
        Assert.assertEquals(string.toString(), "ans='tekst';\n");
    }

    public void testToOctave() throws Exception {
        OctaveType string = new OctaveSqString("tekst");
        Assert.assertEquals(string.toOctave("tre"), "tre='tekst';\n");
    }

    public void testOctaveConnection() throws Exception {
        OctaveType s1 = new OctaveSqString("tekst");
        Octave octave = new Octave();
        octave.set("st", s1);
        OctaveSqString s2 = new OctaveSqString(octave.get("st"));
        Assert.assertEquals(s1, s2);
    }

}

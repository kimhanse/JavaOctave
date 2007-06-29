package dk.ange.octave.type;

import junit.framework.Assert;
import junit.framework.TestCase;
import dk.ange.octave.Octave;

/**
 * @author Kim Hansen
 */
public class TestOctaveString extends TestCase {

    /**
     * @throws Exception
     */
    public void testToString() throws Exception {
        OctaveType string = new OctaveString("tekst");
        Assert.assertEquals("# name: ans\n# type: string\n# elements: 1\n# length: 5\ntekst\n\n", string.toString());
    }

    /**
     * @throws Exception
     */
    public void testToOctave() throws Exception {
        OctaveType string = new OctaveString("mytekst");
        Assert.assertEquals("# name: tre\n# type: string\n# elements: 1\n# length: 7\nmytekst\n\n", string
                .toText("tre"));
    }

    /**
     * @throws Exception
     */
    public void testOctaveConnection() throws Exception {
        OctaveType s1 = new OctaveString("tekst");
        Octave octave = new Octave();
        octave.set("st", s1);
        OctaveString s2 = new OctaveString(octave.get("st"));
        Assert.assertEquals(s1, s2);
    }

}

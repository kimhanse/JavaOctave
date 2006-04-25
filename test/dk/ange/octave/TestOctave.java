package dk.ange.octave;

import java.io.StringReader;

import junit.framework.Assert;
import junit.framework.TestCase;
import dk.ange.octave.type.OctaveScalar;
import dk.ange.octave.type.OctaveType;

/**
 * @author kim
 * 
 * Tests dk.ange.octave.Octave.*
 */
public class TestOctave extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestOctave.class);
    }

    public TestOctave(String name) {
        super(name);
    }

    // Tests:

    /*
     * Octave() and Octave(Writer, Writer) is tested in setUp()
     */

    /*
     * close() is tested in tearDown()
     */

    /*
     * Test method for set(String,double), getScalar(), execute(String)
     */
    public void testExecute() throws Exception {
        Octave octave = new Octave();
        OctaveType X = new OctaveScalar(42);

        octave.set("x", X);
        double x = new OctaveScalar(octave.get("x")).getDouble();
        Assert.assertEquals(42.0, x, 0.0);

        octave.execute("x = x + 10;");
        x = new OctaveScalar(octave.get("x")).getDouble();
        Assert.assertEquals(52.0, x, 0.0);
        octave.close();
    }

    /*
     * Test method for reader=exec(reader)
     */
    public void testExec() throws Exception {
        Octave octave = new Octave();
        octave.set("x", new OctaveScalar(42));
        octave.execute(new StringReader("x=x+10;"));
        double x = new OctaveScalar(octave.get("x")).getDouble();
        assertEquals(52.0, x, 0.0);
        octave.close();
    }

    public void testDestroy() throws Exception {
        Octave octave = new Octave();
        new DestroyThread(octave).start();
        try {
            octave.execute("sleep(10);");
        } catch (OctaveException e) {
            assertTrue(e.isDestroyed());
        }
    }
}

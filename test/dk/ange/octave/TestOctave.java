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

    /**
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestOctave.class);
    }

    /**
     * @param name
     */
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

    /**
     * Test method for set(String,double), getScalar(), execute(String)
     * 
     * @throws Exception
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

    /**
     * Test method for reader=exec(reader)
     * 
     * @throws Exception
     */
    public void testExec() throws Exception {
        Octave octave = new Octave();
        octave.set("x", new OctaveScalar(42));
        octave.execute(new StringReader("x=x+10;"));
        double x = new OctaveScalar(octave.get("x")).getDouble();
        assertEquals(52.0, x, 0.0);
        octave.close();
    }

    /**
     * @throws Exception
     */
    public void testDestroy() throws Exception {
        Octave octave = new Octave();
        new DestroyThread(octave).start();
        try {
            octave.execute("sleep(10);");
        } catch (OctaveException e) {
            assertTrue(e.isDestroyed());
        }
    }

    /**
     * Helper for TestOctave
     * 
     * @author Kim Hansen
     */
    class DestroyThread extends Thread {

        private Octave octave;

        DestroyThread(Octave octave) {
            this.octave = octave;
        }

        @Override
        public void run() {
            try {
                sleep(1000);
                octave.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

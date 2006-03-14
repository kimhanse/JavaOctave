package dk.ange.octave;

import java.io.Reader;
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

    private Octave octave;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        octave = new Octave();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        octave.close();
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
        OctaveType X = new OctaveScalar(42);

        octave.set("x", X);
        double x = new OctaveScalar(octave.get("x")).getDouble();
        Assert.assertEquals(42.0, x, 0.0);

        octave.execute("x = x + 10;");
        x = new OctaveScalar(octave.get("x")).getDouble();
        Assert.assertEquals(52.0, x, 0.0);
    }

    /*
     * Test method for reader=exec(reader)
     */
    public void testExec() throws Exception {
        octave.set("x", new OctaveScalar(42));

        Reader outputReader = octave.execute(new StringReader("x=x+10;"));
        while (outputReader.read() != -1) {
            // slurp
        }
        outputReader.close();

        double x = new OctaveScalar(octave.get("x")).getDouble();
        Assert.assertEquals(52.0, x, 0.0);
    }

}

package dk.ange.octave;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

/**
 * @author Kim Hansen
 */
public class TestOctaveErrors extends TestCase {

    /**
     * @throws Exception
     */
    public void testError() throws Exception {
        final StringWriter stdout = new StringWriter();
        final StringWriter stderr = new StringWriter();
        try {
            final Octave octave = new Octave(null, new PrintWriter(stdout), stderr);
            octave.execute("error('testError()');");
            fail("error in octave should cause execute() to throw an exception");
            octave.close();
        } catch (final OctaveException e) {
            // ok
        }
        stdout.close();
        stderr.close();
        assertEquals("", stdout.toString());
        // FIXME This test fail some times
        assertEquals("This sometime fails, there is some timing problem that prevents all of stderr to get "
                + "from octave to Java when there is an error in octave.", "error: testError()\n", stderr.toString());
    }

    /**
     * @throws Exception
     */
    public void testOk() throws Exception {
        final Octave octave = new Octave();
        octave.execute("ok=1;");
        octave.close();
    }

}

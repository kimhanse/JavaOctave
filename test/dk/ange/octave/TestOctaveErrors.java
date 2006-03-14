package dk.ange.octave;

import java.io.StringWriter;

import junit.framework.TestCase;

public class TestOctaveErrors extends TestCase {

    public void testError() {
        StringWriter stdout = new StringWriter();
        StringWriter stderr = new StringWriter();
        try {
            Octave octave = new Octave(stdout, stderr);
            octave.execute("error('testError()');");
            fail("error in octave should cause execute() to throw an exception");
            octave.close();
        } catch (OctaveException e) {
            // ok
        }
        assertEquals("", stdout.toString());
        assertEquals("error: testError()\n", stderr.toString());
    }

    public void testOk() throws Exception {
        Octave octave = new Octave();
        octave.execute("ok=1;");
        octave.close();
    }

}

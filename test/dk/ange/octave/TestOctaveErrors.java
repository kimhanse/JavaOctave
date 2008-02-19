/*
 * Copyright 2007, 2008 Ange Optimization ApS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.ange.octave;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import dk.ange.octave.exception.OctaveException;

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

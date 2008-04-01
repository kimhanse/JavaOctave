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
/**
 * @author Kim Hansen
 */
package dk.ange.octave.io;

import java.io.OutputStreamWriter;

import junit.framework.TestCase;
import dk.ange.octave.Octave;
import dk.ange.octave.exception.OctaveParseException;
import dk.ange.octave.type.OctaveScalar;

/** Tests */
public class TestUnknownVar extends TestCase {

    /**
     * Test Octave.get() on unknown var
     */
    public void testGetUnknownVar() {
        // FIXME Prevent load of non existing variable from generating output to stderr
        final Octave octave = new Octave(null, new OutputStreamWriter(System.out), null);
        try {
            octave.get("x");
            fail();
        } catch (final OctaveParseException e) {
            // Expect this
        }
        octave.set("x", new OctaveScalar(42));
        octave.get("x");
        octave.close();
    }

}

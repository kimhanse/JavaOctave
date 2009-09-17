/*
 * Copyright 2007, 2008, 2009 Ange Optimization ApS
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
package dk.ange.octave.io.impl;

import junit.framework.TestCase;
import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.exception.OctaveIOException;
import dk.ange.octave.exception.OctaveParseException;
import dk.ange.octave.type.OctaveString;
import dk.ange.octave.type.OctaveType;

/**
 * Test reading of sq_string (can not be written)
 */
public class TestOctaveSqString extends TestCase {

    /** Test */
    public void testEquals() {
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        octave.eval("st='tekst';");
        final OctaveType s2 = octave.get("st");
        final OctaveType s1 = new OctaveString("tekst");
        assertEquals(s1, s2);
        octave.close();
    }

    /** Test */
    public void testUnimplementedEscapeChar() {
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        octave.eval("st='\\\\';");
        try {
            octave.get("st");
            fail();
        } catch (Exception e) {
            // Expect exception
            assertEquals(OctaveParseException.class, e.getClass());
        }
        try {
            octave.close();
            fail();
        } catch (Exception e) {
            // Expect exception
            assertEquals(OctaveIOException.class, e.getClass());
        }
    }

}

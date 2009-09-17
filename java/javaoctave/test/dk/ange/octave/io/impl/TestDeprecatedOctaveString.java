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

import junit.framework.Assert;
import junit.framework.TestCase;
import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.io.OctaveIO;
import dk.ange.octave.type.OctaveString;
import dk.ange.octave.type.OctaveType;

/**
 * Test the @deprecated OctaveString
 */
@Deprecated
public class TestDeprecatedOctaveString extends TestCase {

    /** Test */
    public void testToString() {
        final OctaveType string = new OctaveString("tekst");
        Assert.assertEquals("# name: ans\n# type: string\n# elements: 1\n# length: 5\ntekst\n\n", OctaveIO
                .toText(string));
    }

    /** Test */
    public void testToOctave() {
        final OctaveType string = new OctaveString("mytekst");
        Assert.assertEquals("# name: tre\n# type: string\n# elements: 1\n# length: 7\nmytekst\n\n", OctaveIO.toText(
                string, "tre"));
    }

    /** Test */
    public void testOctaveConnection() {
        final OctaveType s1 = new OctaveString("tekst");
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        octave.put("st", s1);
        final OctaveString s2 = octave.get("st");
        Assert.assertEquals(s1, s2);
    }

}

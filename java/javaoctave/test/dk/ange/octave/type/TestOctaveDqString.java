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
package dk.ange.octave.type;

import junit.framework.TestCase;

/**
 * Test OctaveDqString
 */
public class TestOctaveDqString extends TestCase {

    /** Test */
    public void testValues() {
        final OctaveDqString s1a = new OctaveDqString("1");
        final OctaveDqString s1b = new OctaveDqString("1");
        final OctaveDqString s1c = new OctaveDqString("0");
        s1c.setString("1");

        assertEquals(s1a, s1b);
        assertEquals(s1a, s1c);
        assertEquals(s1b, s1c);
        assertNotSame(s1a, s1b);
        assertNotSame(s1a, s1c);
        assertNotSame(s1b, s1c);

        final OctaveDqString s0 = new OctaveDqString("0");
        final OctaveDqString s2 = new OctaveDqString("2");

        assertFalse(s1a.equals(s0));
        assertFalse(s1a.equals(s2));
    }

}

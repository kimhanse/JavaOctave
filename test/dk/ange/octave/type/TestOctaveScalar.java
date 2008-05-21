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
package dk.ange.octave.type;

import junit.framework.TestCase;

/**
 * @author Kim Hansen
 */
public class TestOctaveScalar extends TestCase {

    /**
     * Test
     */
    public void testValues() {
        final OctaveScalar s1a = new OctaveScalar(1);
        final OctaveScalar s1b = new OctaveScalar(1);
        final OctaveScalar s1c = new OctaveScalar(0);
        s1c.set(1);

        assertEquals(s1a, s1b);
        assertEquals(s1a, s1c);
        assertEquals(s1b, s1c);
        assertNotSame(s1a, s1b);
        assertNotSame(s1a, s1c);
        assertNotSame(s1b, s1c);

        final OctaveScalar s0 = new OctaveScalar(0);
        final OctaveScalar s2 = new OctaveScalar(2);

        assertFalse(s1a.equals(s0));
        assertFalse(s1a.equals(s2));
    }

}

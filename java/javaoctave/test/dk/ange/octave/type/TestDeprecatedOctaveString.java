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
 * Test the @deprecated OctaveString
 */
@Deprecated
public class TestDeprecatedOctaveString extends TestCase {

    /** Test */
    public void testValues() {
        final OctaveString s1a = new OctaveString("1");
        final OctaveString s1b = new OctaveString("1");
        final OctaveString s1c = new OctaveString("0");
        s1c.setString("1");

        assertEquals(s1a, s1b);
        assertEquals(s1a, s1c);
        assertEquals(s1b, s1c);
        assertNotSame(s1a, s1b);
        assertNotSame(s1a, s1c);
        assertNotSame(s1b, s1c);

        final OctaveString s0 = new OctaveString("0");
        final OctaveString s2 = new OctaveString("2");

        assertFalse(s1a.equals(s0));
        assertFalse(s1a.equals(s2));
    }

    /** Test */
    public void testCrossClassEquals() {
        final OctaveString s0 = new OctaveString("0");
        final OctaveDqString d0 = new OctaveString("0");
        final OctaveDqString d1 = new OctaveString("1");
        
        assertEquals(s0, d0);
        assertEquals(d0, s0);
        assertFalse(s0.equals(d1));
        assertFalse(d1.equals(s0));        
    }
    
    /** Test */
    public void testCrossClassHashcode() {
        final OctaveString s0 = new OctaveString("0");
        final OctaveDqString d0 = new OctaveString("0");
        final OctaveDqString d1 = new OctaveString("1");
        
        assertEquals(s0.hashCode(), d0.hashCode());
        assertEquals(d0.hashCode(), s0.hashCode());
        assertFalse(s0.hashCode() == d1.hashCode());
        assertFalse(d1.hashCode() == s0.hashCode());        
    }
    
}

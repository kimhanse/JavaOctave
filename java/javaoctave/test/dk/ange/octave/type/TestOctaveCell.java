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
public class TestOctaveCell extends TestCase {

    /**
     * Tests that the get methods returns a copy
     */
    public void testReturnCopy() {
        final OctaveCell cell = new OctaveCell();
        cell.set(1, 1, new OctaveScalar(2));
        final OctaveScalar scalar = (OctaveScalar) cell.get(1, 1);
        scalar.set(10.0);
        assertEquals(scalar.getDouble(), 10.0);
        assertEquals(((OctaveScalar) cell.get(1, 1)).getDouble(), 2.0);
    }

}

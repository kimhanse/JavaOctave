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
public class TestOctaveMatrix extends TestCase {

    /**
     */
    public void testPerformance() {
        OctaveNdMatrix matrix = new OctaveNdMatrix(30, 0);
        long t = System.currentTimeMillis();
        // 4125 was the number of containers in a real job.
        for (int pos = 1; pos <= 4125; ++pos) {
            matrix.set(4.2, 1, pos);
            matrix.set(4.2, 30, pos);
        }
        long timeused = System.currentTimeMillis() - t;
        if (timeused > 500) {
            fail("Performance test didn't finish in 500ms (used " + timeused + "ms)");
        }

        matrix = new OctaveNdMatrix(0, 30);
        t = System.currentTimeMillis();
        // 700 is just some random number
        for (int pos = 1; pos <= 700; ++pos) {
            matrix.set(4.2, pos, 1);
            matrix.set(4.2, pos, 30);
        }
        timeused = System.currentTimeMillis() - t;
        if (timeused > 500) {
            fail("Performance test didn't finish in 500ms (used " + timeused + "ms)");
        }
    }

}

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

import dk.ange.octave.type.OctaveScalar;

/**
 * @author Kim Hansen
 */
public class RunOctave {

    /**
     * @param args
     */
    public static void main(final String[] args) {
        System.out.println("BEGIN");
        final Octave octave = new Octave();

        octave.set("a", new OctaveScalar(42));
        octave.execute("a");
        System.out.println("Java: a = " + octave.get("a"));
        octave.execute("a=a+10");
        System.out.println("Java: a = " + octave.get("a"));

        octave.close();
        System.out.println("END.");
    }

}

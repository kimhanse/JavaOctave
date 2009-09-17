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

import dk.ange.octave.type.OctaveNdMatrix;
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
        test1();
        test2();
        System.out.println("END.");
    }

    private static void test1() {
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();

        octave.put("a", new OctaveScalar(42));
        octave.eval("a");
        System.out.println("Java: a = " + octave.get("a"));
        octave.eval("a=a+10");
        System.out.println("Java: a = " + octave.get("a"));

        octave.close();
    }

    private static void test2() {
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        final OctaveNdMatrix a = new OctaveNdMatrix(new double[] { 1, 2, 3, 4 }, 2, 2);
        octave.put("a", a);
        final String func = "" //
                + "function res = my_func(a)\n" //
                + "  res = 2 * a;\n" //
                + "endfunction\n" //
                + "";
        octave.eval(func);
        octave.eval("b = my_func(a);");
        final OctaveNdMatrix b = octave.get("b");
        octave.close();

        System.out.println("Java: b(1,1) = " + b.get(1,1));
        System.out.println("Java: b(1,2) = " + b.get(1,2));
        System.out.println("Java: b(2,1) = " + b.get(2,1));
        System.out.println("Java: b(2,2) = " + b.get(2,2));
    }

}

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
package dk.ange.octave.io.impl;

import junit.framework.Assert;
import junit.framework.TestCase;
import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.io.OctaveIO;
import dk.ange.octave.type.OctaveCell;
import dk.ange.octave.type.OctaveScalar;
import dk.ange.octave.type.OctaveDqString;
import dk.ange.octave.type.OctaveStruct;
import dk.ange.octave.type.OctaveType;

/**
 * @author Kim Hansen
 */
public class TestOctaveStruct extends TestCase {

    /**
     */
    public void testConstructor() {
        final OctaveType struct = new OctaveStruct();
        Assert.assertEquals("# name: mystruct\n# type: struct\n# length: 0\n", OctaveIO.toText(struct, "mystruct"));
    }

    /**
     */
    public void testSet() {
        final OctaveStruct struct1 = new OctaveStruct();
        struct1.set("a", new OctaveScalar(42));
        Assert.assertEquals("" + //
                "# name: mystruct\n" + //
                "# type: struct\n" + //
                "# length: 1\n" + //
                "# name: a\n" + //
                "# type: cell\n" + //
                "# rows: 1\n" + //
                "# columns: 1\n" + //
                "# name: <cell-element>\n" + //
                "# type: scalar\n" + //
                "42.0\n\n" // 
        , OctaveIO.toText(struct1, "mystruct"));
        final OctaveStruct struct2 = new OctaveStruct();
        final OctaveCell octaveCell = new OctaveCell();
        octaveCell.set(1, 1, new OctaveScalar(42));
        struct2.set("mycell", octaveCell);
        Assert.assertEquals("" + //
                "# name: mystruct\n" + //
                "# type: struct\n" + //
                "# length: 1\n" + //
                "# name: mycell\n" + //
                "# type: cell\n" + //
                "# rows: 1\n" + //
                "# columns: 1\n" + //
                "# name: <cell-element>\n" + //
                "# type: cell\n" + //
                "# rows: 1\n" + //
                "# columns: 1\n" + //
                "# name: <cell-element>\n" + //
                "# type: scalar\n" + //
                "42.0\n\n\n" // 
        , OctaveIO.toText(struct2, "mystruct"));
    }

    /**
     */
    public void testOctaveConnection() {
        final OctaveStruct struct = new OctaveStruct();
        struct.set("scalar", new OctaveScalar(42));
        final OctaveStruct nested_struct = new OctaveStruct();
        nested_struct.set("string", new OctaveDqString("a cheese called Horace"));
        struct.set("mynestedstruct", nested_struct);

        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        octave.put("mystruct", struct);
        final OctaveStruct mystruct_copy = octave.get("mystruct");
        Assert.assertEquals(struct, mystruct_copy);
    }

    /**
     * Test
     */
    public void testMatices() {
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        octave.eval("s = struct();");
        final int[] i123 = { 1, 2, 3 };
        for (final int i : i123) {
            octave.eval(setMatrix(i));
            for (final int j : i123) {
                octave.eval(setMatrix(i, j));
                for (final int k : i123) {
                    octave.eval(setMatrix(i, j, k));
                    for (final int l : i123) {
                        octave.eval(setMatrix(i, j, k, l));
                    }
                }
            }
        }
        final OctaveStruct s1 = octave.get("s");
        octave.put("s1", s1);
        octave.eval("t = 1.0*isequal(s, s1);"); // "1.0*" is a typecast from bool to scalar
        final OctaveScalar t = octave.get("t");
        assertEquals(1.0, t.getDouble());
        final OctaveStruct s2 = octave.get("s1");
        assertEquals(s1, s2);
        octave.close();
    }

    private String setMatrix(final int... sizes) {
        final StringBuilder b = new StringBuilder();
        b.append("s.x");
        for (final int s : sizes) {
            b.append(Integer.toString(s));
        }
        b.append(" = round(1000*rand(");
        boolean first = true;
        for (final int s : sizes) {
            if (first) {
                first = false;
            } else {
                b.append(", ");
            }
            b.append(Integer.toString(s));
        }
        b.append("))/1000;");
        return b.toString();
    }

}

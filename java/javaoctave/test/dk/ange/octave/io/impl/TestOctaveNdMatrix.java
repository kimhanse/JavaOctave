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

import java.util.TreeMap;

import junit.framework.TestCase;
import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.io.OctaveIO;
import dk.ange.octave.type.OctaveNdMatrix;
import dk.ange.octave.type.OctaveStruct;
import dk.ange.octave.type.OctaveType;

/**
 * @author Esben Mose Hansen
 */
public class TestOctaveNdMatrix extends TestCase {

    /** Test */
    public void testSetAndGetOctave() {
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        final TreeMap<String, OctaveType> vars = new TreeMap<String, OctaveType>();
        final double[] bigdata = new double[2 * 3 * 4];
        for (int idx = 0; idx < bigdata.length; idx++) {
            bigdata[idx] = idx + 1.0;
        }
        final double[] data2d = { 1.0, 2.0, 3.0, 5.0, 8.0, 13.0 };
        final double[] datascalar = { 42.0 };
        vars.put("bigmatrix", new OctaveNdMatrix(bigdata, 1, 2, 3, 4));
        vars.put("matrix2d", new OctaveNdMatrix(data2d, 2, 3));
        vars.put("matrixscalar", new OctaveNdMatrix(datascalar, 1, 1));
        vars.put("matrixzero", new OctaveNdMatrix(0, 0, 0, 0));
        vars.put("matrixzero2d", new OctaveNdMatrix(0, 0));
        octave.putAll(vars);
        final OctaveNdMatrix matrixzero = octave.get("matrixzero");
        final OctaveNdMatrix matrix2d = octave.get("matrix2d");
        final OctaveNdMatrix bigmatrix = octave.get("bigmatrix");
        final OctaveNdMatrix matrixzero2d = octave.get("matrixzero2d");
        final OctaveNdMatrix matrixscalar = octave.get("matrixscalar");
        assertEquals(matrixzero, vars.get("matrixzero"));
        assertEquals(matrixzero2d, vars.get("matrixzero2d"));
        assertEquals(matrixscalar, vars.get("matrixscalar"));
        assertEquals(matrix2d, vars.get("matrix2d"));
        assertEquals(bigmatrix, vars.get("bigmatrix"));
        octave.close();

        assertEquals("" + //
                "# name: matrixzero2d\n" + //
                "# type: matrix\n" + //
                "# rows: 0\n" + //
                "# columns: 0\n\n" //
        , OctaveIO.toText(matrixzero2d, "matrixzero2d"));

        assertEquals("" + //
                "# name: matrixzero\n" + //
                "# type: matrix\n" + //
                "# ndims: 4\n" + //
                " 0 0 0 0\n\n" // 
        , OctaveIO.toText(matrixzero, "matrixzero"));

        assertEquals("" + //
                "# name: matrixscalar\n" + //
                "# type: matrix\n" + //
                "# rows: 1\n" + //
                "# columns: 1\n" + //
                " 42.0\n\n" //
        , OctaveIO.toText(matrixscalar, "matrixscalar"));

        assertEquals("" + //
                "# name: matrix2d\n" + //
                "# type: matrix\n" + //
                "# rows: 2\n" + //
                "# columns: 3\n" + //
                " 1.0 3.0 8.0\n" + //
                " 2.0 5.0 13.0\n\n" //
        , OctaveIO.toText(matrix2d, "matrix2d"));

        assertEquals("" + //
                "# name: bigmatrix\n" + //
                "# type: matrix\n" + //
                "# ndims: 4\n" + //
                " 1 2 3 4\n" + //
                " 1.0\n" + //
                " 2.0\n" + //
                " 3.0\n" + //
                " 4.0\n" + //
                " 5.0\n" + //
                " 6.0\n" + //
                " 7.0\n" + //
                " 8.0\n" + //
                " 9.0\n" + //
                " 10.0\n" + //
                " 11.0\n" + //
                " 12.0\n" + //
                " 13.0\n" + //
                " 14.0\n" + //
                " 15.0\n" + //
                " 16.0\n" + //
                " 17.0\n" + //
                " 18.0\n" + //
                " 19.0\n" + //
                " 20.0\n" + //
                " 21.0\n" + //
                " 22.0\n" + //
                " 23.0\n" + //
                " 24.0\n\n" //
        , OctaveIO.toText(bigmatrix, "bigmatrix"));
    }

    /**
     * Test
     */
    public void testScalar() {
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        octave.eval("s.one=1;");
        octave.eval("s.two=[2 3];");

        final OctaveStruct cell = octave.get("s");

        final OctaveNdMatrix two = (OctaveNdMatrix) cell.get("two");
        assertEquals(1, two.rows());
        assertEquals(2, two.columns());
        assertEquals(2d, two.get(1, 1));
        assertEquals(3d, two.get(1, 2));

        final OctaveNdMatrix one = (OctaveNdMatrix) cell.get("one");
        assertEquals(1, one.rows());
        assertEquals(1, one.columns());
        assertEquals(1d, one.get(1, 1));

        octave.close();
    }

}

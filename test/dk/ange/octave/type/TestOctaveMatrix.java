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

import java.io.OutputStreamWriter;
import java.io.StringWriter;

import junit.framework.Assert;
import junit.framework.TestCase;
import dk.ange.octave.Octave;
import dk.ange.octave.OctaveIO;

/**
 * @author Kim Hansen
 */
public class TestOctaveMatrix extends TestCase {

    /**
     * @throws Exception
     */
    public void testConstructor() throws Exception {
        final OctaveNdMatrix matrix = new OctaveNdMatrix(0, 0);
        Assert.assertEquals(0, matrix.rows());
        Assert.assertEquals(0, matrix.columns());
        Assert.assertEquals("" + //
                "# name: matrix\n" + //
                "# type: matrix\n" + //
                "# rows: 0\n" + //
                "# columns: 0\n" + //
                "\n" //
        , OctaveIO.toText(matrix, "matrix"));
    }

    /**
     * @throws Exception
     */
    public void testConstructorMatrix() throws Exception {
        final double[] numbers = { 1, 2, 3, 4, 5, 6 };
        final OctaveNdMatrix matrix = new OctaveNdMatrix(numbers, 2, 3);
        Assert.assertEquals(2, matrix.rows());
        Assert.assertEquals(3, matrix.columns());
        Assert.assertEquals("" + //
                "# name: mymatrix\n" + //
                "# type: matrix\n" + //
                "# rows: 2\n" + //
                "# columns: 3\n" + //
                " 1.0 3.0 5.0\n" + //
                " 2.0 4.0 6.0\n\n" //
        , OctaveIO.toText(matrix, "mymatrix"));
    }

    /**
     * @throws Exception
     */
    public void testConstructorIntInt() throws Exception {
        final OctaveNdMatrix matrix = new OctaveNdMatrix(2, 3);
        Assert.assertEquals(2, matrix.rows());
        Assert.assertEquals(3, matrix.columns());
        Assert.assertEquals("" + //
                "# name: matrix\n" + //
                "# type: matrix\n" + //
                "# rows: 2\n" + //
                "# columns: 3\n" + //
                " 0.0 0.0 0.0\n" + //
                " 0.0 0.0 0.0\n\n" //
        , OctaveIO.toText(matrix, "matrix"));
        matrix.set(42, 1, 2);
        Assert.assertEquals("" + //
                "# name: myother\n" + //
                "# type: matrix\n" + //
                "# rows: 2\n" + //
                "# columns: 3\n" + //
                " 0.0 42.0 0.0\n" + //
                " 0.0 0.0 0.0\n\n" //
        , OctaveIO.toText(matrix, "myother"));
        matrix.set(2, 2, 1);
        Assert.assertEquals("" + //
                "# name: myother\n" + //
                "# type: matrix\n" + //
                "# rows: 2\n" + //
                "# columns: 3\n" + //
                " 0.0 42.0 0.0\n" + //
                " 2.0 0.0 0.0\n\n" //
        , OctaveIO.toText(matrix, "myother"));
        matrix.set(4.0, 2, 2);
        Assert.assertEquals("" + //
                "# name: myother\n" + //
                "# type: matrix\n" + //
                "# rows: 2\n" + //
                "# columns: 3\n" + //
                " 0.0 42.0 0.0\n" + //
                " 2.0 4.0 0.0\n\n" //
        , OctaveIO.toText(matrix, "myother"));
    }

    /**
     * @throws Exception
     */
    public void testGrowth() throws Exception {
        final OctaveNdMatrix matrix = new OctaveNdMatrix(0, 0);
        Assert.assertEquals(0, matrix.rows());
        Assert.assertEquals(0, matrix.columns());
        Assert.assertEquals("" + //
                "# name: matrix\n" + //
                "# type: matrix\n" + //
                "# rows: 0\n" + //
                "# columns: 0\n\n" //
        , OctaveIO.toText(matrix, "matrix"));
        matrix.set(1, 1, 1);
        Assert.assertEquals(1, matrix.rows());
        Assert.assertEquals(1, matrix.columns());
        Assert.assertEquals("" + //
                "# name: matrix\n" + //
                "# type: matrix\n" + //
                "# rows: 1\n" + //
                "# columns: 1\n" + //
                " 1.0\n\n" //
        , OctaveIO.toText(matrix, "matrix"));
        matrix.set(3, 3, 1);
        Assert.assertEquals(3, matrix.rows());
        Assert.assertEquals(1, matrix.columns());
        Assert.assertEquals("" + //
                "# name: matrix\n" + //
                "# type: matrix\n" + //
                "# rows: 3\n" + //
                "# columns: 1\n" + //
                " 1.0\n" + //
                " 0.0\n" + //
                " 3.0\n\n" //
        , OctaveIO.toText(matrix, "matrix"));

        final OctaveNdMatrix matrix2 = new OctaveNdMatrix(0, 0);
        matrix2.set(3.0, 1, 3);
        Assert.assertEquals("" + //
                "# name: matrix\n" + //
                "# type: matrix\n" + //
                "# rows: 1\n" + //
                "# columns: 3\n" + //
                " 0.0 0.0 3.0\n" + //
                "\n" //
        , OctaveIO.toText(matrix2, "matrix"));
    }

    /**
     * @throws Exception
     */
    public void testOctaveGet() throws Exception {
        final Octave octave = new Octave();
        octave.execute("m=[1 2;3 4];");
        final OctaveNdMatrix m = octave.get("m");
        Assert.assertEquals("" + //
                "# name: m\n" + //
                "# type: matrix\n" + //
                "# rows: 2\n" + //
                "# columns: 2\n" + //
                " 1.0 2.0\n" + //
                " 3.0 4.0\n" + //
                "\n" //
        , OctaveIO.toText(m, "m"));
    }

    /**
     * @throws Exception
     */
    public void testOctaveSetExecGet() throws Exception {
        final double[] numbers = { 1, 2, 3, 4, 5, 6 };
        final Octave octave = new Octave();
        final OctaveNdMatrix in = new OctaveNdMatrix(numbers, 2, 3);
        octave.set("in", in);
        octave.execute("out=in;");
        final OctaveNdMatrix out = octave.get("out");
        Assert.assertEquals(OctaveIO.toText(in), OctaveIO.toText(out));
        octave.execute("slicerow=in(2,:); slicecol=in(:,2);");
        final OctaveNdMatrix slicerow = octave.get("slicerow");
        final OctaveNdMatrix slicecol = octave.get("slicecol");
        assertEquals(2.0, slicerow.get(1, 1));
        assertEquals(4.0, slicerow.get(1, 2));
        assertEquals(6.0, slicerow.get(1, 3));
        assertEquals(3.0, slicecol.get(1, 1));
        assertEquals(4.0, slicecol.get(2, 1));

    }

    /**
     * @throws Exception
     */
    public void testPerformance() throws Exception {
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

    /**
     * Test how the system handles save of Inf and NaN
     * 
     * @throws Exception
     */
    public void testSaveNanInf() throws Exception {
        final StringWriter stderr = new StringWriter();
        final Octave octave = new Octave(null, new OutputStreamWriter(System.out), stderr);
        octave.execute("ok=1;");
        @SuppressWarnings("unused")
        OctaveScalar ok;
        octave.execute("xnan=[NaN 0];");
        ok = octave.get("ok");
        final OctaveNdMatrix xnan = octave.get("xnan");
        assertEquals(Double.NaN, xnan.get(1, 1));
        assertEquals(Double.valueOf(0), xnan.get(1, 2));
        ok = octave.get("ok");
        octave.execute("xinf=[Inf -Inf];");
        ok = octave.get("ok");
        final OctaveNdMatrix xinf = octave.get("xinf");
        assertEquals(Double.POSITIVE_INFINITY, xinf.get(1, 1));
        assertEquals(Double.NEGATIVE_INFINITY, xinf.get(1, 2));
        ok = octave.get("ok");
        octave.close();
        stderr.close();
        assertEquals("", stderr.toString()); // No warning when saving matrix with NaN/Inf
    }

}

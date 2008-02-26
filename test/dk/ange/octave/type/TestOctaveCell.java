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

import junit.framework.Assert;
import junit.framework.TestCase;
import dk.ange.octave.Octave;

/**
 * @author Kim Hansen
 */
public class TestOctaveCell extends TestCase {

    /**
     * @throws Exception
     */
    public void testConstructor() throws Exception {
        final OctaveCell cell = new OctaveCell();
        Assert.assertEquals(0, cell.getRowDimension());
        Assert.assertEquals(0, cell.getColumnDimension());
        Assert.assertEquals("# name: ans\n# type: cell\n# rows: 0\n# columns: 0\n", cell.toText("ans"));
    }

    /**
     * @throws Exception
     */
    public void testConstructorValue() throws Exception {
        final OctaveCell cell = new OctaveCell();
        cell.set(1, 1, new OctaveScalar(42));
        Assert.assertEquals(1, cell.getRowDimension());
        Assert.assertEquals(1, cell.getColumnDimension());
        Assert.assertEquals("# name: mycell2\n# type: cell\n# rows: 1\n# columns: 1\n"
                + "# name: <cell-element>\n# type: scalar\n42.0\n\n\n" //
        , cell.toText("mycell2"));
    }

    /**
     * @throws Exception
     */
    public void testConstructorIntInt() throws Exception {
        final OctaveCell cell = new OctaveCell(2, 2);
        Assert.assertEquals(2, cell.getRowDimension());
        Assert.assertEquals(2, cell.getColumnDimension());
        Assert.assertEquals("# name: mycell22\n# type: cell\n# rows: 2\n# columns: 2\n"
                + "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "\n" //
        , cell.toText("mycell22"));
    }

    /**
     * @throws Exception
     */
    public void testSetIntInt() throws Exception {
        final OctaveCell cell = new OctaveCell();
        Assert.assertEquals(0, cell.getRowDimension());
        Assert.assertEquals(0, cell.getColumnDimension());
        cell.set(3, 4, new OctaveScalar(42));
        Assert.assertEquals(3, cell.getRowDimension());
        Assert.assertEquals(4, cell.getColumnDimension());
        Assert.assertEquals("# name: mycell\n# type: cell\n# rows: 3\n# columns: 4\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: scalar\n42.0\n\n\n" //
        , cell.toText("mycell"));
    }

    /**
     * Tests that the get methods returns a copy
     * 
     * @throws Exception
     */
    public void testReturnCopy() throws Exception {
        final OctaveCell cell = new OctaveCell();
        cell.set(1, 1, new OctaveScalar(2));
        final OctaveScalar scalar = (OctaveScalar) cell.get(1, 1);
        scalar.set(10.0);
        assertEquals(scalar.getDouble(), 10.0);
        assertEquals(((OctaveScalar) cell.get(1, 1)).getDouble(), 2.0);
    }

    /**
     * @throws Exception
     */
    public void testOctaveConnection() throws Exception {
        final OctaveCell cell = new OctaveCell();
        cell.set(1, 1, new OctaveScalar(42));
        final OctaveCell cell2 = new OctaveCell();
        cell2.set(1, 1, new OctaveString("mystring"));
        cell.set(3, 2, cell2);

        final Octave octave = new Octave();
        octave.set("mycell", cell);
        final OctaveCell mycell_copy = octave.get("mycell");
        assertEquals(cell, mycell_copy);
    }

    /**
     * @throws Exception
     */
    public void testSameInOctave() throws Exception {
        final OctaveCell cell = new OctaveCell(2, 3);
        for (int r = 1; r <= 2; ++r) {
            for (int c = 1; c <= 3; ++c) {
                cell.set(r, c, new OctaveScalar(r + 0.1 * c));
            }
        }
        final Octave octave = new Octave();
        octave.set("cell_java", cell);
        // Assert it is the same in Octave and Java
        octave.execute("cell_octave=cell(); for r=1:2 for c=1:3 cell_octave{r,c}=r+0.1*c; endfor endfor");
        octave.execute("assert(cell_octave, cell_java);");
        // Assert that the returned value is the same as the original
        assertEquals(cell, octave.get("cell_java"));
        octave.close();
    }

}

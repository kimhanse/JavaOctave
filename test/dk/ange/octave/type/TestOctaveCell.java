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
        OctaveCell cell = new OctaveCell();
        Assert.assertEquals(0, cell.getRowDimension());
        Assert.assertEquals(0, cell.getColumnDimension());
        Assert.assertEquals("# name: ans\n# type: cell\n# rows: 0\n# columns: 0\n", cell.toText("ans"));
    }

    /**
     * @throws Exception
     */
    public void testConstructorValue() throws Exception {
        OctaveCell cell = new OctaveCell(new OctaveScalar(42));
        Assert.assertEquals(1, cell.getRowDimension());
        Assert.assertEquals(1, cell.getColumnDimension());
        Assert.assertEquals("# name: mycell2\n# type: cell\n# rows: 1\n# columns: 1\n" +
                "# name: <cell-element>\n# type: scalar\n42.0\n\n\n" //
                , cell.toText("mycell2"));
    }

    /**
     * @throws Exception
     */
    public void testConstructorIntInt() throws Exception {
        OctaveCell cell = new OctaveCell(2, 2);
        Assert.assertEquals(2, cell.getRowDimension());
        Assert.assertEquals(2, cell.getColumnDimension());
        Assert.assertEquals("# name: mycell22\n# type: cell\n# rows: 2\n# columns: 2\n" +
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
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
        OctaveCell cell = new OctaveCell();
        Assert.assertEquals(0, cell.getRowDimension());
        Assert.assertEquals(0, cell.getColumnDimension());
        cell.set(3, 4, new OctaveScalar(42));
        Assert.assertEquals(3, cell.getRowDimension());
        Assert.assertEquals(4, cell.getColumnDimension());
        Assert.assertEquals("# name: mycell\n# type: cell\n# rows: 3\n# columns: 4\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: matrix\n# rows: 0\n# columns: 0\n\n" + //
                "# name: <cell-element>\n# type: scalar\n42.0\n\n\n" //
        , cell.toText("mycell"));
    }
    
    /**
     * Tests that the get methods returns a copy
     * @throws Exception
     */
    public void testReturnCopy() throws Exception {
        OctaveCell cell = new OctaveCell(new OctaveScalar(2.0));
        OctaveScalar scalar = (OctaveScalar) cell.get(1,1);
        scalar.set(10.0);
        assertEquals(scalar.getDouble(), 10.0); 
        assertEquals(((OctaveScalar) cell.get(1,1)).getDouble(),2.0);
    }

    /**
     * @throws Exception
     */
    public void testOctaveConnection() throws Exception {
        OctaveCell cell = new OctaveCell(new OctaveScalar(42));
        cell.set(3, 2, new OctaveCell(new OctaveString("mystring")));
        
        Octave octave = new Octave();
        octave.set("mycell", cell);
        OctaveCell mycell_copy = new OctaveCell(octave.get("mycell"));
        assertEquals(cell, mycell_copy);
    }

}

package dk.ange.octave.type;

import junit.framework.Assert;
import junit.framework.TestCase;

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
        Assert.assertEquals("ans=cell(0,0);\n", cell.toOctave("ans"));
    }

    /**
     * @throws Exception
     */
    public void testConstructorValue() throws Exception {
        OctaveCell cell = new OctaveCell(new OctaveScalar(42));
        Assert.assertEquals(1, cell.getRowDimension());
        Assert.assertEquals(1, cell.getColumnDimension());
        Assert.assertEquals("ans=cell(1,1);\n" //
                + "clear octave_java_tmp_cell;\n" //
                + "octave_java_tmp_cell=42.0;\n" //
                + "ans{1,1}=octave_java_tmp_cell;\n" //
                + "clear octave_java_tmp_cell;\n", //
                cell.toOctave("ans"));
    }

    /**
     * @throws Exception
     */
    public void testConstructorIntInt() throws Exception {
        OctaveCell cell = new OctaveCell(2, 2);
        Assert.assertEquals(2, cell.getRowDimension());
        Assert.assertEquals(2, cell.getColumnDimension());
        Assert.assertEquals("ans=cell(2,2);\n", //
                cell.toOctave("ans"));
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
        Assert.assertEquals("ans=cell(3,4);\n" //
                + "clear octave_java_tmp_cell;\n" //
                + "octave_java_tmp_cell=42.0;\n" //
                + "ans{3,4}=octave_java_tmp_cell;\n" //
                + "clear octave_java_tmp_cell;\n", //
                cell.toOctave("ans"));
    }

}

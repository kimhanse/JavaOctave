package dk.ange.octave.type;

import dk.ange.octave.Octave;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Kim Hansen
 */
public class TestOctaveStruct extends TestCase {

    /**
     * @throws Exception
     */
    public void testConstructor() throws Exception {
        OctaveType struct = new OctaveStruct();
        Assert.assertEquals("# name: mystruct\n# type: struct\n# length: 0\n", struct.toText("mystruct"));

    }

    /**
     * @throws Exception
     */
    public void testSet() throws Exception {
        OctaveStruct struct1 = new OctaveStruct();
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
        , struct1.toText("mystruct"));
        OctaveStruct struct2 = new OctaveStruct();
        struct2.set("mycell", new OctaveCell(new OctaveScalar(42)));
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
        , struct2.toText("mystruct"));
    }

    /**
     * @throws Exception
     */
    public void testOctaveConnection() throws Exception {
        OctaveStruct struct = new OctaveStruct();
        struct.set("scalar", new OctaveScalar(42));
        OctaveStruct nested_struct = new OctaveStruct();
        nested_struct.set("string", new OctaveString("a cheese called Horace"));
        struct.set("mynestedstruct", nested_struct);
        
        Octave octave = new Octave();
        octave.set("mystruct", struct);
        OctaveStruct mystruct_copy = new OctaveStruct(octave.get("mystruct"));
        Assert.assertEquals(struct, mystruct_copy);
    }

    /**
     * @throws Exception
     */
    public void testOctaveGetCopy() throws Exception {
        OctaveStruct struct = new OctaveStruct();
        struct.set("scalar", new OctaveScalar(42));
        OctaveScalar scalar =  (OctaveScalar)struct.get("scalar");
        scalar.set(10);
        assertEquals(scalar.getDouble(), 10.0);
        assertEquals(((OctaveScalar)struct.get("scalar")).getDouble(), 42.0);
    }
}

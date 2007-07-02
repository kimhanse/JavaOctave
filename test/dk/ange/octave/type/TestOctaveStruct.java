package dk.ange.octave.type;

import junit.framework.Assert;
import junit.framework.TestCase;
import dk.ange.octave.Octave;

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
        OctaveScalar scalar = (OctaveScalar) struct.get("scalar");
        scalar.set(10);
        assertEquals(scalar.getDouble(), 10.0);
        assertEquals(((OctaveScalar) struct.get("scalar")).getDouble(), 42.0);
    }

    /**
     * Test
     * @throws Exception
     */
    public void testMatices() throws Exception {
        Octave octave = new Octave();
        octave.execute("s = struct();");
        final int[] i123 = { 1, 2, 3 };
        for (int i : i123) {
            octave.execute(setMatrix(i));
            for (int j : i123) {
                octave.execute(setMatrix(i, j));
                for (int k : i123) {
                    octave.execute(setMatrix(i, j, k));
                    for (int l : i123) {
                        octave.execute(setMatrix(i, j, k, l));
                    }
                }
            }
        }
        OctaveStruct s1 = new OctaveStruct(octave.get("s"));
        octave.set("s1", s1);
        octave.execute("t = 1.0*isequal(s, s1);"); // "1.0*" is a typecast from bool to scalar
        OctaveScalar t = new OctaveScalar(octave.get("t"));
        assertEquals(1.0, t.getDouble());
        OctaveStruct s2 = new OctaveStruct(octave.get("s1"));
        assertEquals(s1, s2);
        octave.close();
    }

    private String setMatrix(int... sizes) {
        StringBuilder b = new StringBuilder();
        b.append("s.x");
        for (int s : sizes) {
            b.append(Integer.toString(s));
        }
        b.append(" = round(1000*rand(");
        boolean first = true;
        for (int s : sizes) {
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

package dk.ange.octave.type;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestOctaveStruct extends TestCase {

    public void testConstructor() throws Exception {
        OctaveStruct struct = new OctaveStruct();
        Assert.assertEquals("ans=struct();\n", struct.toOctave("ans"));
    }

    public void testSet() throws Exception {
        OctaveStruct struct = new OctaveStruct();
        struct.set("a", new OctaveScalar(42));
        Assert.assertEquals("ans=struct();\n" //
                + "clear octave_java_tmp_struct;\n" //
                + "octave_java_tmp_struct=42.0;\n" //
                + "ans.a=octave_java_tmp_struct;\n" //
                + "clear octave_java_tmp_struct;\n" //
                + "", struct.toOctave("ans"));
    }

}

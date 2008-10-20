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

import java.io.StringWriter;

import junit.framework.Assert;
import junit.framework.TestCase;
import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.io.OctaveIO;
import dk.ange.octave.type.OctaveScalar;
import dk.ange.octave.type.OctaveType;

/**
 * @author Kim Hansen
 */
public class TestOctaveScalar extends TestCase {

    /**
     * @throws Exception
     */
    public void testToString() throws Exception {
        final OctaveType integer = new OctaveScalar(42);
        Assert.assertEquals("# name: ans\n# type: scalar\n42.0\n\n", OctaveIO.toText(integer));
    }

    /**
     * @throws Exception
     */
    public void testToOctave() throws Exception {
        final OctaveType integer = new OctaveScalar(43);
        Assert.assertEquals("# name: tre\n# type: scalar\n43.0\n\n", OctaveIO.toText(integer, "tre"));
    }

    /**
     * @throws Exception
     */
    public void testOctaveConnection() throws Exception {
        final OctaveType i1 = new OctaveScalar(42);
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        octave.put("i", i1);
        final OctaveScalar i2 = octave.get("i");
        Assert.assertEquals(i1, i2);
        octave.close();
    }

    /**
     * Test how the system handles save of Inf and NaN
     * 
     * @throws Exception
     */
    public void testSaveNanInf() throws Exception {
        final StringWriter stderr = new StringWriter();
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        octave.setErrorWriter(stderr);

        octave.eval("ok=1;");
        final OctaveScalar okOne = new OctaveScalar(1);
        OctaveScalar ok;

        octave.eval("xnan=NaN;");
        ok = octave.get("ok");
        assertEquals(okOne, ok);
        final OctaveScalar xnan = octave.get("xnan");
        assertEquals(Double.NaN, xnan.getDouble());
        ok = octave.get("ok");
        assertEquals(okOne, ok);

        octave.eval("xinf=Inf;");
        ok = octave.get("ok");
        assertEquals(okOne, ok);
        final OctaveScalar xinf = octave.get("xinf");
        assertEquals(Double.POSITIVE_INFINITY, xinf.getDouble());
        ok = octave.get("ok");
        assertEquals(okOne, ok);

        octave.eval("xninf=-Inf;");
        ok = octave.get("ok");
        assertEquals(okOne, ok);
        final OctaveScalar xninf = octave.get("xninf");
        assertEquals(Double.NEGATIVE_INFINITY, xninf.getDouble());
        ok = octave.get("ok");
        assertEquals(okOne, ok);

        octave.close();
        stderr.close();
        assertEquals("", stderr.toString());
    }

    /** Test that we can get and set globals */
    public void testGlobal() {
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();

        octave.eval("global x");
        octave.put("x", new OctaveScalar(42.0));

        final OctaveScalar x = octave.get("x");
        assertEquals(42.0, x.get(1));

        octave.close();
    }

}

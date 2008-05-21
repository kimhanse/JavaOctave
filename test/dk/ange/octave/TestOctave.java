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
/**
 * @author Kim Hansen
 */
package dk.ange.octave;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.Assert;
import junit.framework.TestCase;
import dk.ange.octave.exception.OctaveException;
import dk.ange.octave.exception.OctaveIOException;
import dk.ange.octave.type.OctaveScalar;
import dk.ange.octave.type.OctaveType;

/**
 * Tests dk.ange.octave.Octave.*
 */
public class TestOctave extends TestCase {

    /**
     * @param args
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(TestOctave.class);
    }

    /**
     * @param name
     */
    public TestOctave(final String name) {
        super(name);
    }

    // Tests:

    /*
     * Octave() and Octave(Writer, Writer) is tested in setUp()
     */

    /*
     * close() is tested in tearDown()
     */

    /**
     * Test method for set(String,double), getScalar(), execute(String)
     * 
     * @throws Exception
     */
    public void testExecute() throws Exception {
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();

        final OctaveType Y = new OctaveScalar(2);
        octave.put("y", Y);
        final OctaveType X = new OctaveScalar(42);
        octave.put("x", X);
        final OctaveType Z = new OctaveScalar(4);
        octave.put("z", Z);

        OctaveScalar x = octave.get("x");
        Assert.assertEquals(42.0, x.getDouble(), 0.0);

        octave.eval("x = x + 10;");
        x = octave.get("x");
        Assert.assertEquals(52.0, x.getDouble(), 0.0);
        octave.close();
    }

    /**
     * Test method for reader=exec(reader)
     * 
     * @throws Exception
     */
    public void testExec() throws Exception {
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        octave.put("x", new OctaveScalar(42));
        octave.eval(new StringReader("x=x+10;"));
        final OctaveScalar octaveScalar = octave.get("x");
        final double x = octaveScalar.getDouble();
        assertEquals(52.0, x, 0.0);
        octave.close();
    }

    /**
     * @throws Exception
     */
    public void testDestroy() throws Exception {
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        octave.eval("sigterm_dumps_octave_core(0);");
        new DestroyThread(octave).start();
        try {
            octave.eval("sleep(10);");
        } catch (final OctaveException e) {
            assertTrue(e.isDestroyed());
        }
    }

    /**
     * Helper for TestOctave
     * 
     * @author Kim Hansen
     */
    private static class DestroyThread extends Thread {
        private final OctaveEngine octave;

        private DestroyThread(final OctaveEngine octave) {
            this.octave = octave;
        }

        @Override
        public void run() {
            try {
                sleep(1000);
                octave.destroy();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Test advanced Constructor to Octave()
     * 
     * TODO Detect path instead of having it hardcoded
     * 
     * @throws Exception
     */
    public void testConstructor() throws Exception {
        final OctaveEngineFactory octaveEngineFactory = new OctaveEngineFactory();
        octaveEngineFactory.setOctaveProgram(new File("/usr/bin/octave"));
        final OctaveEngine octave = octaveEngineFactory.getScriptEngine();
        octave.setWriter(null);
        octave.eval("disp('testConstructor');");
        octave.close();
    }

    /**
     * Test if files are closed by the Octave object
     * 
     * @throws Exception
     */
    public void testFileClose() throws Exception {
        final Writer stdin = new DontCloseWriter("stdin");
        final Writer stdout = new DontCloseWriter("stdout");
        final Writer stderr = new DontCloseWriter("stderr");
        final OctaveEngineFactory octaveEngineFactory = new OctaveEngineFactory();
        octaveEngineFactory.setErrorWriter(stderr);
        octaveEngineFactory.setOctaveInputLog(stdin);
        final OctaveEngine octave = octaveEngineFactory.getScriptEngine();
        octave.setWriter(stdout);
        octave.eval("disp('testFileClose');");
        octave.close();

        final OctaveEngine octave2 = octaveEngineFactory.getScriptEngine();
        octave.setWriter(stdout);
        try {
            octave2.eval("error('testFileClose2');");
            fail();
        } catch (final OctaveException e) {
            assertTrue(e instanceof OctaveIOException);
        }
    }

    private static class DontCloseWriter extends Writer {
        private final String name;

        private DontCloseWriter(final String name) {
            this.name = name;
        }

        @Override
        public void write(final char[] cbuf, final int off, final int len) {
            // Don't do anything
        }

        @Override
        public void flush() {
            // Don't do anything
        }

        @Override
        public void close() throws IOException {
            throw new IOException("DontCloseWriter '" + name + "' closed.");
        }
    }

    /**
     * Test
     */
    public void testOutputWithoutNewline() {
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        final StringWriter result = new StringWriter();
        octave.setWriter(result);
        octave.eval("printf('testOutputWithoutNewline1');");
        assertEquals("testOutputWithoutNewline1", result.toString());
        result.getBuffer().setLength(0);
        octave.eval("disp('testOutputWithoutNewline2');");
        assertEquals("testOutputWithoutNewline2\n", result.toString());
        result.getBuffer().setLength(0);
        octave.eval("printf('testOutput\\nWithoutNewline3');");
        assertEquals("testOutput\nWithoutNewline3", result.toString());
        result.getBuffer().setLength(0);
        octave.eval("disp('testOutput\\nWithoutNewline4');");
        assertEquals("testOutput\\nWithoutNewline4\n", result.toString());
        result.getBuffer().setLength(0);
        octave.eval("disp(\"testOutput\\nWithoutNewline5\");");
        assertEquals("testOutput\nWithoutNewline5\n", result.toString());
        result.getBuffer().setLength(0);
        octave.eval("'testOutputWithoutNewline6'");
        assertEquals("ans = testOutputWithoutNewline6\n", result.toString());
        octave.close();
    }

}

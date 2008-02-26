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
import java.io.Writer;
import java.util.HashMap;

import junit.framework.Assert;
import junit.framework.TestCase;
import dk.ange.octave.exception.OctaveException;
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
        final Octave octave = new Octave();
        final HashMap<String, OctaveType> typelist = new HashMap<String, OctaveType>();
        final OctaveType Y = new OctaveScalar(2);
        typelist.put("y", Y);
        final OctaveType X = new OctaveScalar(42);
        typelist.put("x", X);
        final OctaveType Z = new OctaveScalar(4);
        typelist.put("z", Z);

        octave.set(typelist);
        OctaveScalar x = octave.get("x");
        Assert.assertEquals(42.0, x.getDouble(), 0.0);

        octave.execute("x = x + 10;");
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
        final Octave octave = new Octave();
        octave.set("x", new OctaveScalar(42));
        octave.execute(new StringReader("x=x+10;"));
        final OctaveScalar octaveScalar = octave.get("x");
        final double x = octaveScalar.getDouble();
        assertEquals(52.0, x, 0.0);
        octave.close();
    }

    /**
     * @throws Exception
     */
    public void testDestroy() throws Exception {
        final Octave octave = new Octave();
        octave.execute("sigterm_dumps_octave_core(0);");
        new DestroyThread(octave).start();
        try {
            octave.execute("sleep(10);");
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
        private final Octave octave;

        private DestroyThread(final Octave octave) {
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
     * Test null input to Octave()
     * 
     * @throws Exception
     */
    public void testNullInput() throws Exception {
        final Octave octave = new Octave(null, null, null);
        octave.execute("disp('Test');");
        octave.close();
    }

    /**
     * Test advanced Constructor to Octave()
     * 
     * TODO Detect path instead of having it hardcoded
     * 
     * @throws Exception
     */
    public void testConstructor() throws Exception {
        final Octave octave = new Octave(null, null, null, new File("/usr/bin/octave"), null, null);
        octave.execute("disp('Test');");
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
        final Octave octave = new Octave(stdin, stdout, stderr);
        octave.execute("disp('Test');");
        octave.close();
        final Octave octave2 = new Octave(stdin, stdout, stderr);
        try {
            octave2.execute("error('Test');");
            fail();
        } catch (final OctaveException e) {
            assertTrue(e.getCause() instanceof IOException);
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

}

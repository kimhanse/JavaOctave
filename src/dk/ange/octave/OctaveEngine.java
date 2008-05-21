/*
 * Copyright 2008 Ange Optimization ApS
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
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;

import dk.ange.octave.exception.OctaveClassCastException;
import dk.ange.octave.exec.OctaveExec;
import dk.ange.octave.exec.ReadFunctor;
import dk.ange.octave.exec.ReaderWriteFunctor;
import dk.ange.octave.exec.WriteFunctor;
import dk.ange.octave.exec.WriterReadFunctor;
import dk.ange.octave.io.OctaveIO;
import dk.ange.octave.type.OctaveType;

/**
 * The connection to an octave process.
 * 
 * This is inspired by the javax.script.ScriptEngine interface.
 */
public final class OctaveEngine {

    private final OctaveEngineFactory factory;

    private final OctaveExec octaveExec;

    private final OctaveIO octaveIO;

    private Writer writer = new OutputStreamWriter(System.out);

    OctaveEngine(final OctaveEngineFactory factory, final Writer octaveInputLog, final Writer errorWriter,
            final File octaveProgram, final File workingDir) {
        this.factory = factory;
        octaveExec = new OctaveExec(octaveInputLog, errorWriter, octaveProgram, null, workingDir);
        octaveIO = new OctaveIO(octaveExec);
    }

    /**
     * @param script
     *                the script to execute
     */
    public void eval(final String script) {
        octaveExec.eval(new WriteFunctor() {
            public void doWrites(final Writer writer2) throws IOException {
                writer2.write(script);
            }
        }, getReadFunctor());
    }

    private ReadFunctor getReadFunctor() {
        if (writer == null) {
            // If writer is null create a "do nothing" functor
            return new ReadFunctor() {
                private final char[] buffer = new char[4096];

                public void doReads(final Reader reader) throws IOException {
                    while (reader.read(buffer) != -1) {
                        // Do nothing
                    }
                }
            };
        } else {
            return new WriterReadFunctor(writer);
        }
    }

    /**
     * @param script
     *                the script to execute
     */
    public void eval(final Reader script) {
        octaveExec.eval(new ReaderWriteFunctor(script), getReadFunctor());
    }

    /**
     * Sets a value in octave.
     * 
     * @param key
     *                the name of the variable
     * @param value
     *                the value to set
     */
    public void put(final String key, final OctaveType value) {
        octaveIO.set(Collections.singletonMap(key, value));
    }

    /**
     * Sets all the mappings in the specified map as variables in octave. These mappings replace any variable that
     * octave had for any of the keys currently in the specified map.
     * 
     * @param vars
     *                the variables to be stored in octave
     */
    public void putAll(final Map<String, OctaveType> vars) {
        octaveIO.set(vars);
    }

    /**
     * @param <T>
     *                the type of the return value
     * @param key
     *                the name of the variable
     * @return the value from octave or null if the variable does not exist
     * @throws OctaveClassCastException
     *                 If the value can not be cast to T
     */
    @SuppressWarnings("unchecked")
    public <T extends OctaveType> T get(final String key) {
        return (T) octaveIO.get(key);
    }

    /**
     * @return the factory that created this object
     */
    public OctaveEngineFactory getFactory() {
        return factory;
    }

    /**
     * Set the writer that the scripts output will be written to.
     * 
     * This method is usually placed in ScriptContext.
     * 
     * @param writer
     *                the writer to set
     */
    public void setWriter(final Writer writer) {
        this.writer = writer;
    }

    /**
     * Set the writer that the scripts error output will be written to.
     * 
     * This method is usually placed in ScriptContext.
     * 
     * @param errorWriter
     *                the errorWriter to set
     */
    public void setErrorWriter(final Writer errorWriter) {
        octaveExec.setErrorWriter(errorWriter);
    }

    /**
     * Close the octave process in an orderly fasion.
     */
    public void close() {
        octaveExec.close();
    }

    /**
     * Kill the octave process without remorse.
     */
    public void destroy() {
        octaveExec.destroy();
    }

}
